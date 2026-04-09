package com.dna.jopt.rest.client.util.webhook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Minimal standalone webhook test server.
 *
 * Listens on http://localhost:{PORT}/webhook for POST requests.
 *
 * <h3>Standalone usage:</h3>
 * <pre>
 *   javac WebhookTestServer.java
 *   java WebhookTestServer [port] [secret]
 * </pre>
 *
 * Defaults: port=9000, secret="" (unsigned, no verification)
 *
 * <h3>Programmatic usage:</h3>
 * <pre>
 *   WebhookTestServer server = new WebhookTestServer(9000, "mySecret");
 *   server.start();
 *   String payload = server.waitForWebhook().get(5, TimeUnit.MINUTES);
 *   server.stop();
 * </pre>
 *
 * Point TourOptimizer at:
 *   completionWebhookUrl:    http://host.docker.internal:9000/webhook
 *   completionWebhookSecret: (same secret value as passed here, or leave both empty)
 *
 * The server prints each incoming webhook to stdout with:
 *   - Received timestamp
 *   - All request headers
 *   - Raw payload body
 *   - Signature verification result (if a secret was provided)
 *
 * Responds 200 OK to every valid POST so TourOptimizer logs delivery as successful.
 * Responds 405 Method Not Allowed to anything other than POST.
 * Responds 200 to GET /webhook/ping for a quick liveness check.
 */
public class WebhookTestServer {

    private static final String HMAC_ALGORITHM  = "HmacSHA256";
    private static final String SIGNATURE_HEADER = "x-jopt-signature";

    private final int port;
    private final String secret;
    private HttpServer server;
    private WebhookHandler handler;

    public WebhookTestServer(int port, String secret) {
        this.port = port;
        this.secret = secret;
    }

    /**
     * Starts the webhook server. Non-blocking.
     */
    public void start() throws IOException {
        this.handler = new WebhookHandler(secret);
        this.server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        this.server.createContext("/webhook", handler);
        this.server.start();

        System.out.println("=======================================================");
        System.out.println("  JOpt Webhook Test Server");
        System.out.println("=======================================================");
        System.out.println("  Listening on : http://localhost:" + port + "/webhook");
        System.out.println("  Ping         : http://localhost:" + port + "/webhook/ping");
        System.out.println("  Signing      : " + (secret.isBlank() ? "DISABLED (no secret)" : "ENABLED"));
        System.out.println();
        System.out.println("  Docker target URL:");
        System.out.println("    http://host.docker.internal:" + port + "/webhook");
        System.out.println("=======================================================");
        System.out.println("Waiting for webhooks...");
        System.out.println();
    }

    /**
     * Stops the webhook server immediately.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Webhook server stopped.");
        }
    }

    /**
     * Returns a {@link CompletableFuture} that completes with the webhook
     * payload body when the next POST to /webhook is received.
     * Each call returns the same future until a webhook arrives; after
     * that a new future is created for subsequent calls.
     */
    public CompletableFuture<String> waitForWebhook() {
        return handler.getWebhookFuture();
    }

    /**
     * Standalone entry point.
     */
    public static void main(String[] args) throws IOException {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 9000;
        String secret = args.length > 1 ? args[1] : "YourStr0ng!Secret_Here";

        WebhookTestServer webhookServer = new WebhookTestServer(port, secret);
        webhookServer.start();
    }

    static class WebhookHandler implements HttpHandler {

        private final String secret;
        private final AtomicInteger counter = new AtomicInteger(0);
        private volatile CompletableFuture<String> webhookFuture = new CompletableFuture<>();

        WebhookHandler(String secret) {
            this.secret = secret;
        }

        CompletableFuture<String> getWebhookFuture() {
            return webhookFuture;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String path   = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            // Liveness ping — useful for verifying Docker networking before running a job
            if ("GET".equalsIgnoreCase(method) && path.endsWith("/ping")) {
                respond(exchange, 200, "pong");
                System.out.println("[PING] " + Instant.now());
                return;
            }

            if (!"POST".equalsIgnoreCase(method)) {
                respond(exchange, 405, "Method Not Allowed");
                return;
            }

            int num = counter.incrementAndGet();
            String received = Instant.now().toString();

            // Read body
            byte[] bodyBytes;
            try (InputStream in = exchange.getRequestBody()) {
                bodyBytes = in.readAllBytes();
            }
            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            // Collect headers
            Map<String, List<String>> headers = exchange.getRequestHeaders();

            // Signature verification
            String sigHeader = headers.getOrDefault(
                SIGNATURE_HEADER,
                headers.getOrDefault("X-JOpt-Signature", List.of("(missing)"))
            ).get(0);

            String verificationResult;
            if (secret.isBlank()) {
                verificationResult = "SKIPPED (no secret configured)";
            } else if ("sha256=unsigned".equals(sigHeader)) {
                verificationResult = "UNSIGNED — payload was not signed by server";
            } else {
                verificationResult = verifySignature(bodyBytes, sigHeader)
                        ? "OK — signature valid"
                        : "FAILED — signature mismatch (wrong secret?)";
            }

            // Print to console
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.printf ("║  Webhook #%-3d received at %s%n", num, received);
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║  Headers:");
            for (Map.Entry<String, List<String>> h : headers.entrySet()) {
                System.out.printf("║    %-30s %s%n", h.getKey() + ":", h.getValue());
            }
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║  Body:");
            // Pretty-print JSON by inserting newlines after { , }
            String pretty = body
                .replace(",\"", ",\n    \"")
                .replace("{\"", "{\n    \"")
                .replace("}", "\n}");
            for (String line : pretty.split("\n")) {
                System.out.println("║    " + line);
            }
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║  Signature verification: " + verificationResult);
            System.out.println("╚══════════════════════════════════════════════════╝");
            System.out.println();

            respond(exchange, 200, "OK");

            // Complete the future so programmatic callers get notified
            CompletableFuture<String> current = webhookFuture;
            current.complete(body);
            // Prepare a new future for any subsequent webhook
            webhookFuture = new CompletableFuture<>();
        }

        private boolean verifySignature(byte[] bodyBytes, String sigHeader) {
            try {
                Mac mac = Mac.getInstance(HMAC_ALGORITHM);
                mac.init(new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
                String expected = "sha256=" + HexFormat.of().formatHex(
                        mac.doFinal(bodyBytes));
                // Constant-time comparison
                return constantTimeEquals(expected, sigHeader);
            } catch (Exception e) {
                System.out.println("[ERROR] Signature verification failed: " + e.getMessage());
                return false;
            }
        }

        /** Constant-time string comparison to prevent timing attacks. */
        private static boolean constantTimeEquals(String a, String b) {
            if (a.length() != b.length()) return false;
            int result = 0;
            for (int i = 0; i < a.length(); i++) {
                result |= a.charAt(i) ^ b.charAt(i);
            }
            return result == 0;
        }

        private static void respond(HttpExchange exchange, int status, String body)
                throws IOException {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
