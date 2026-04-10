# JOpt.TourOptimizer Java REST Client Examples

<a href="https://dna-evolutions.com/" target="_blank"><img src="https://www.dna-evolutions.com/images/dna_logo.png" width="200" title="DNA Evolutions" alt="DNA Evolutions"></a>

A fully functional Maven project demonstrating how to interact with the **JOpt.TourOptimizer REST API** from Java. Examples cover the full job lifecycle submitting optimizations, tracking progress via Server-Sent Events, retrieving results, and working with the synchronous run mode. The generated client is built on Spring WebFlux, giving you reactive `Flux` and `Mono` access to every endpoint.

- **Documentation hub:** [dna-evolutions.com/docs/learn-and-explore/rest/rest_client_touroptimizer](https://dna-evolutions.com/docs/learn-and-explore/rest/rest_client_touroptimizer)
- **TourOptimizer server guide:** [dna-evolutions.com/docs/learn-and-explore/rest/rest-server-touroptimizer](https://dna-evolutions.com/docs/learn-and-explore/rest/rest-server-touroptimizer)
- **Sandbox quickstart:** [dna-evolutions.com/docs/getting-started/quickstart/jopt_sandboxes_quickstart](https://dna-evolutions.com/docs/getting-started/quickstart/jopt_sandboxes_quickstart)
- **Interactive API:** [dna-evolutions.com/api](https://dna-evolutions.com/api/)
- **Java SDK examples:** [github.com/DNA-Evolutions/Java-TourOptimizer-Examples](https://github.com/DNA-Evolutions/Java-TourOptimizer-Examples)
- **JavaDocs:** [public.javadoc.dna-evolutions.com](https://public.javadoc.dna-evolutions.com)

> Requires **JOpt.TourOptimizer 1.3.5 or higher**.

---

## Overview

- [What is JOpt.TourOptimizer](#what-is-jopt-touroptimizer)
- [Example categories](#example-categories)
- [Getting started](#getting-started)
- [Browser sandbox (Docker)](#browser-sandbox-docker)
- [Clone and run locally](#clone-and-run-locally)
- [How the REST client is generated](#how-the-rest-client-is-generated)
- [Troubleshooting](#troubleshooting)
- [Further links](#further-links)

---

## What is JOpt.TourOptimizer

JOpt.TourOptimizer is DNA Evolutions' route optimization and scheduling engine, available as a containerised REST service built on Spring WebFlux. It exposes a reactive OpenAPI 3 contract and can be hosted locally via Docker or accessed via a cloud endpoint.

Click to watch the introduction:

<a href="https://www.youtube.com/watch?v=U4mDQGnZGZs" target="_blank"><img src="https://dna-evolutions.com/images/docs/home/jopt_intro_prev.gif" width="600"
title="Introduction to JOpt" alt="Introduction to JOpt"></a>

---

## Example categories

The project is subdivided into the following example packages. Each has its own README.

| Package | Description |
|---|---|
| [`touroptimizer`](https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/touroptimizer) | Core optimization examples submit jobs, poll status, stream progress, retrieve results |
| [`fullstack`](https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/fullstack) | End-to-end examples combining multiple API features |
| [`secretscreation`](https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/secretscreation) | Create the `secrets.json` file required by all examples |

---

## Getting started

Two ways to start:

1. **[Browser sandbox](#browser-sandbox-docker)** zero installation, runs in your browser via Docker.
2. **[Clone locally](#clone-and-run-locally)** import as a Maven project in your IDE.

In either case you need a `secrets.json` file and a running TourOptimizer instance. Run `SecretsCreatorExampleHelper` from the [`secretscreation`](https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/secretscreation) package once to generate it.

---

## Browser sandbox (Docker)

The sandbox is a browser-based IDE ([code-server](https://github.com/cdr/code-server) / Visual Studio Code) with Java, Maven, and the latest examples pre-installed. The generated client classes are built automatically on the first run.

**Docker Hub:** [`dnaevolutions/jopt_rest_example_server`](https://hub.docker.com/r/dnaevolutions/jopt_rest_example_server)

### Start the sandbox

```bash
docker run -it -d \
  --name jopt-rest-examples \
  -p 127.0.0.1:8043:8080 \
  -v "$PWD/:/home/coder/project" \
  dnaevolutions/jopt_rest_example_server:latest
```

The `-v` flag mounts your current directory so your changes and `secrets.json` survive restarts.

### Open the sandbox

Navigate to [http://localhost:8043](http://localhost:8043) and log in with:

```
joptrest
```

On the first run Maven downloads dependencies and generates all client classes — this takes a few minutes. A [tutorial video](https://www.youtube.com/watch?v=-LT1xxzrpBE) (~3 minutes) walks through the full sandbox workflow.

**System requirements:** at least 2 GB RAM and 2 CPU cores.

### Start TourOptimizer alongside the sandbox

The examples need a running TourOptimizer instance. The simplest approach is to start TourOptimizer on your host and reach it from inside the sandbox container via `host.docker.internal`:

```bash
docker run -d --rm --name jopt-touroptimizer \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=cors \
  dnaevolutions/jopt_touroptimizer:latest

docker run -it -d \
  --name jopt-rest-examples \
  -p 127.0.0.1:8043:8080 \
  -v "$PWD/:/home/coder/project" \
  dnaevolutions/jopt_rest_example_server:latest
```

From inside the sandbox, point the client at `http://host.docker.internal:8081` instead of `localhost`.

Alternatively, run both containers on a shared Docker network so the sandbox can reach TourOptimizer by container name:

```bash
docker network create jopt-network

docker run -d --rm --name jopt-touroptimizer \
  --network jopt-network \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=cors \
  dnaevolutions/jopt_touroptimizer:latest

docker run -it -d \
  --name jopt-rest-examples \
  --network jopt-network \
  -p 127.0.0.1:8043:8080 \
  -v "$PWD/:/home/coder/project" \
  dnaevolutions/jopt_rest_example_server:latest
```

When using a Docker network, point the client at `http://jopt-touroptimizer:8081`.

> **Job mode requires MongoDB.** Examples that use the job endpoints (`POST /api/v1/jobs`, `GET /api/v1/jobs/{jobId}/result`, etc.) require a connected MongoDB instance. Without it, job endpoints are inactive and only the synchronous run endpoints (`POST /api/v1/runs`) are available. See the [TourOptimizer server guide](https://dna-evolutions.com/docs/learn-and-explore/rest/rest-server-touroptimizer) for the full Docker Compose setup including MongoDB.

---

## Clone and run locally

### Prerequisites

- Java 17 or later
- Maven 3.6 or later
- A running TourOptimizer instance (see [Docker REST TourOptimizer](https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer))

### Steps

```bash
git clone https://github.com/DNA-Evolutions/Java-REST-Client-Examples.git
```

Import the project as a Maven project in your IDE. On the first build the OpenAPI generator plugin creates all client classes automatically from the bundled spec files. Then run `SecretsCreatorExampleHelper` once, and run any example's `main` method.

---

## How the REST client is generated

The client classes are auto-generated at build time from the OpenAPI 3 spec files in `src/main/resources/swagger/` using the [`openapi-generator-maven-plugin`](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin). You never need to edit these files manually.

The key configuration in `pom.xml` uses the `webclient` library to produce Spring WebFlux-compatible types:

```xml
<configuration>
    <generatorName>java</generatorName>
    <typeMappings>
        <typeMapping>OffsetDateTime=Instant</typeMapping>
    </typeMappings>
    <importMappings>
        <importMapping>java.time.OffsetDateTime=java.time.Instant</importMapping>
    </importMappings>
    <library>webclient</library>
</configuration>
```

This gives you reactive `Mono<T>` and `Flux<T>` return types on every generated API method, compatible with the TourOptimizer's Spring WebFlux backend.

To regenerate from a newer spec, replace the JSON file in `src/main/resources/swagger/` and run `mvn generate-sources`.

---

## Troubleshooting

**`NoSecretFileFoundException`**

```
There is no secret file present in: .../secrets/secrets.json
```

You have not created the secrets file yet. Run `SecretsCreatorExampleHelper` from the `secretscreation` package once.

---

**`Connection refused: localhost/127.0.0.1:8081`**

```
finishConnect(..) failed: Connection refused: localhost/127.0.0.1:8081
```

If you are running the examples inside the sandbox container, `localhost` refers to the container itself, not your host machine. Use `http://host.docker.internal:8081` instead — this is the simplest fix and requires no network configuration. Alternatively, run TourOptimizer on a shared Docker network and use its container name (e.g. `http://jopt-touroptimizer:8081`). The example `TourOptimizerSimpleLocalDockerExample` in the `touroptimizer/dockerhosted` package is pre-configured for the `host.docker.internal` endpoint.

---

## Further links

### Documentation

- [REST client and TourOptimizer guide](https://dna-evolutions.com/docs/learn-and-explore/rest/rest_client_touroptimizer)
- [TourOptimizer server guide](https://dna-evolutions.com/docs/learn-and-explore/rest/rest-server-touroptimizer)
- [Sandbox quickstart](https://dna-evolutions.com/docs/getting-started/quickstart/jopt_sandboxes_quickstart)
- [Getting started](https://www.dna-evolutions.com/docs/getting-started)
- [Special features overview](https://dna-evolutions.com/docs/learn-and-explore/special/special_features)
- [Interactive API testing](https://dna-evolutions.com/api/)
- [JavaDocs](https://public.javadoc.dna-evolutions.com)

### Code and registry

- [TourOptimizer Docker repository](https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer)
- [Java SDK examples](https://github.com/DNA-Evolutions/Java-TourOptimizer-Examples)
- [Nexus repository](https://nexus.dna-evolutions.net)

### Evaluation license

Get an extended free license designed for small businesses and customers evaluating the product:  
[DNA Evolutions Portal](https://www.dna-evolutions.com/portal) *(sign-in required)*

### Social

- [LinkedIn](https://www.linkedin.com/company/dna-evolutions/)
- [Docker Hub](https://hub.docker.com/u/dnaevolutions)
- [YouTube tutorials](https://www.youtube.com/channel/UCzfZjJLp5Rrk7U2UKsOf8Fw)
- [SourceForge](https://sourceforge.net/software/product/JOpt.TourOptimizer/)

---

## Contact

For help or questions reach us at [www.dna-evolutions.com/contact](https://www.dna-evolutions.com/contact) or [info@dna-evolutions.com](mailto:info@dna-evolutions.com).

---

## Agreement

For license terms and plans please visit [www.dna-evolutions.com](https://www.dna-evolutions.com).

---

A product by [DNA Evolutions GmbH](https://www.dna-evolutions.com) &copy;