package com.dna.jopt.rest.client.example.touroptimizer.dockerhosted;

import com.dna.jopt.rest.client.example.secretscreation.SecretsCreatorExampleHelper;
import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.TextSolution;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TourOptimizerSimpleLocalDockerExample {
    /**
     * The main method of TourOptimizerSimpleLocalDockerExample
     *
     * This is a simple example that can be run while both, the REST-Client-Examples sandbox and the TourOptimizer are
     * locally hosted in Docker.
     *
     * Before running, {@link SecretsCreatorExampleHelper} has to be executed in order to generate a secrets.json with
     * the public license.
     *
     * @param args the arguments
     * @throws IOException                Signals that an I/O exception has
     *                                    occurred.
     * @throws NoSecretFileFoundException the no secret file found exception
     * @throws SecretNotFoundException    the secret not found exception
     */
    public static void main(String[] args) throws IOException, NoSecretFileFoundException, SecretNotFoundException {

        /*
         * Please refer to https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer
         * on how to setup a local running Rest TourOptimizer and getting more
         * information
         *
         * Quick Start: ============== 1) You can also execute:
         *
         * docker run -d --rm --name myJOptTourOptimizer -e
         * SPRING_PROFILES_ACTIVE="cors" -p 8081:8081 dnaevolutions/jopt_touroptimizer
         *
         * from any console on any machine with reasonable new docker environment.
         *
         * 2) Visit http://localhost:8081/ from any browser, and see if service came up
         *
         * 3) You can run this example :-)
         *
         *
         */

        /*
         *
         * Modify me
         *
         */
        boolean isSave2JSON = true;

        // We take a small dataset, that does not exceed the 15
        // elements limit of the free default license

        // Define the data to be used
        // Nodes are the jobs that need to be done
        List<Position> nodePoss = new ArrayList<>();

        nodePoss.add(new Position().latitude(50.776351).longitude(6.083862).locationId("Node_0_Aachen"));
        nodePoss.add(new Position().latitude(50.938361).longitude(6.959974).locationId("Node_1_Cologne"));
        nodePoss.add(new Position().latitude(51.4582235).longitude(7.0158171).locationId("Node_2_Essen"));
        nodePoss.add(new Position().latitude(52.5170365).longitude(13.3888599).locationId("Node_3_Berlin"));
        nodePoss.add(new Position().latitude(52.8455492).longitude(13.2461296).locationId("Node_4_Brandenburg"));
        nodePoss.add(new Position().latitude(52.3744779).longitude(9.7385532).locationId("Node_5_Hannover"));

        // Resources are the workers, who do the jobs at the Nodes
        List<Position> resourcePoss = new ArrayList<>();
        resourcePoss.add(new Position().latitude(50.8031684).longitude(6.4820806).locationId("Resource_0_Dueren"));
        resourcePoss.add(new Position().latitude(51.8666527).longitude(12.646761).locationId("Resource_1_Wittenberg"));


        /*
         *
         *
         */

        // This example assumes you are hosting the RestTourOptimizer on your local machine at
        // port 8081. Of course you can use any port you want, but you need to modify
        // the endpoint url
        //
        // BY default we use:
        // LOCAL_SWAGGER_TOUROPTIMIZER_URL = "http://localhost:8081", but in this example the endpoint has been
        // modified to "http://host.docker.internal:8081" to allow the two Docker containers of the Rest-examples
        // and the TourOptimizer to communicate on the same local machine.

        TourOptimizerRestCaller tourOptimizerCaller = new TourOptimizerRestCaller(
                Endpoints.LOCAL_Docker_SWAGGER_TOUROPTIMIZER_URL);

        /*
         *
         * Optimize
         *
         */
        Optional<TextSolution> textSolutionOpt;

        // Connections are a means to provide additional information about the journey between two Nodes like the exact
        // distance or traffic.
        // Here, we simply provide empty connections => This will trigger automatic haversine
        // distance calculations

        List<ElementConnection> emptyConnections = new ArrayList<>();

        SecretsManager m = new SecretsManager();
        RestOptimization result = tourOptimizerCaller.optimize(nodePoss, resourcePoss, emptyConnections,
                Optional.of(m.get("joptlic")));

        textSolutionOpt = Optional.ofNullable(result.getExtension().getTextSolution());

        // Show result
        textSolutionOpt.ifPresent(ts -> {
            System.out.println("Printing text solution");
            System.out.println(ts);
        });

        /*
         * Save to JSON snapshot
         */
        if (isSave2JSON) {

            String jsonFile = "OptimizationLocalTest" + ".json";

            RestJSONParser.toJsonFile(result, new File(jsonFile), tourOptimizerCaller.getMapper());
        }

    }

}