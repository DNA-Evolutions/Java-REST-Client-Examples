# Java-REST-Client-Examples by DNA-Evolutions



<a href="https://dna-evolutions.com/" target="_blank"><img src="https://docs.dna-evolutions.com/indexres/dna-temp-logo.png" width="110"
title="DNA-Evolutions" alt="DNA-Evolutions"></a>


Containerizing an application helps to use it more conveniently across different platforms and, most importantly, as a microservice. Further, scaling an application becomes more straightforward as various standardized orchestration tools can be utilized. A Microservice can be launched either (locally) or, for example, as a highly-scalable web-micro-service in a Kubernetes cluster.


# Short Introduction
This repository is part of our JOpt-REST-Suite. It provides examples of how to set up a REST client in Java to access the following DNA Evolution's web services:

- JOpt-TourOptimizer based on JOpt-Core (available as local Container and via Azure)
- JOpt-GeoCoder based on JOpt-Core and <a href="https://github.com/pelias/pelias" target="_blank">Pelias</a> (available via Azure)
- JOpt-RoutePlanner based on JOpt-Core and <a href="https://github.com/valhalla/valhalla" target="_blank">Valhalla</a>  (available via Azure)

You can find an extensive collection of examples for the core library in our <a href="https://github.com/DNA-Evolutions/Java-TourOptimizer-Examples" target="_blank">Java-TourOptimizer-Examples repository</a>. Please let us know if you need help to port an example and run it by a REST-Service.

The service can be called via an API-Key using our Microsoft Azure-Kubernetes Infrastructure. If you are interested in hosting our JOpt-REST-GeoCoder and JOpt-REST-GeoRouter products in your environment, please get in touch with us.

All our RESTful Services utilise <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html" target="_blank">Spring WebFlux</a> and <a href="https://swagger.io/" target="_blank">Swagger</a>. Internally the Java version of TourOptimizer is used. Indeed all specifications for the different services are derived from the core library, leading to guaranteed compatibility between all three services.

<a href="https://dna-evolutions.com/" target="_blank"><img src="https://docs.dna-evolutions.com/indexres/dna-evolutions-product-infographic-jopt-cloud-integration-highres.svg" width="600"
title="DNA-Evolutions Integration" alt="DNA-Evolutions Integration"></a>

### JOpt-GeoCoder
Forward-/Reverse geocode Addresses or Positions.

### JOpt-RoutePlanner
Find distances and driving times between points. Either as turn-by-turn result optionally providing driving instructions and route shapes or as matrix request for multiple routings.

### JOpt-TourOptimizer
Optimize a problem consisting of Nodes, Resources, and optionally externally provided connections. In contrast to our other services, we allow you to host your JOpt-TourOptimizer locally.

---

# Outline of this repository

The project is subdivided into five major types of examples:

1. <a href="https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/fullstack" target="_blank">Fullstack examples</a>
2. <a href="https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/geocoder" target="_blank">GeoCoder creation</a>
3. <a href="https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/routeplanner" target="_blank">Route Planner Examples</a>
4. <a href="https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/touroptimizer" target="_blank">TourOptimizer Examples</a>
5. <a href="https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/secretscreation" target="_blank">Secrets creation</a>

Each of the example-sections has its own README.

---


# Architecture of the generated REST-Client-API

The REST-Client class files used by the examples of this repository will be automatically generated from the provided API-docs <a href="https://swagger.io/specification/" target="_blank">(OpenAPI 3 Specification)</a> in the resources swagger folder once the project gets built utilizing the <a href="https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin" target="_blank">openapi-generator-maven-plugin</a>  by <a href="https://github.com/OpenAPITools" target="_blank">OpenAPI Tools</a>.

OpenApi Generator allows setting some generation configuration. Indeed, it is vital to adjust the generator to slightly create the correct client files.

As we use the maven-plugin, we define the configuration directly inside the `pom.xml`. For example, for our TourOptimizer endpoint, we use:

<configuration>

```xml
<configuration>
	...
	<generatorName>java</generatorName>
	
	<typeMappings>
		<typeMapping>OffsetDateTime=Instant</typeMapping>
	</typeMappings>
	
	<importMappings>
		<importMapping>java.time.OffsetDateTime=java.time.Instant</importMapping>
	</importMappings>
	
	<library>webclient</library>
	
	...
</configuration>
```

Please see the `pom.xml` for the entire configuration description. Besides some mappings, we use the WebClient library to enable <a href="https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html" target="_blank">Flux</a> and <a href="https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html" target="_blank">Mono</a> compatible access to our swagger endpoints.

You can also generate a client in the programming language of your choice utilizing our API-docs. REST facilitates software integration in your desired language (including famous ones like C#, Java, JS, Scala, Python, and many more ). Don't hesitate to contact us if you need help setting up your client.

---

# Getting started

You can start using our examples in different ways.

* [Use our sandbox in your browser (Docker required)](#use-our-sandbox-in-your-browser-docker-required)
* [Clone this repository](#clone-this-repository)

In any case, you need to provide a `secrets.json` file. Please follow the README.md file inside the <a href="https://github.com/DNA-Evolutions/Java-REST-Client-Examples/tree/master/src/main/java/com/dna/jopt/rest/client/example/secretscreation" target="_blank">Secrets creation package</a>.


## Prerequisites

* In your IDE as native Java dependency: Install at least Java 8, Maven
* In our sandbox: Working Docker environment


## Clone this repository
Clone this repository, import it as Maven project in your IDE, create a `screts.json` and start any example.


## Use our sandbox in your browser (Docker required)
If you want to get started without the hassle of installing Java, Maven and an IDE, we provide a sandbox. The sandbox is based on  [code-server](https://github.com/cdr/code-server) and can be used inside your browser, and the interface itself is based on Visual Code. The sandbox is available via DockerHub ([here](https://hub.docker.com/r/dnaevolutions/jopt_rest_example_server)). You have to host the sandbox in your Docker environment (Please provide at least 2-4Gb of Ram and 2 Cores). You can pull the sandbox from our DockerHub account (The Dockerfile for creating the sandbox is included in this repository). The latest version of our examples is cloned by default on launching the Docker container, and you can start testing JOpt-REST right away.


### Starting the sandbox and persist your changes
You must mount a volume to which the examples of this project are downloaded on the container's startup. After re-launching the container, the latest version of our examples is only cloned if the folder is not already existing, keeping your files safe from being overridden.

Launching a sanbox and mount your current directory ('$PWD') or any other directory you want:

```
docker run -it -d --name jopt-rest-examples -p 127.0.0.1:8043:8080 -v "$PWD/:/home/coder/project" dnaevolutions/jopt_rest_example_server:latest
```

### Using the sandbox

After starting the container, you can open [http://localhost:8043/](http://localhost:8043) with your browser and login with the password:

```
joptrest
```

During the run of your first example file, some dependencies are downloaded, and it will take some time (below 1 minute depending on your internet connection). In case you need help, contact us.

---

# Further Documentation, Contact and Links

- Further documentation 	- <a href="https://docs.dna-evolutions.com" target="_blank">docs.dna-evolutions.com</a>
- Special features 	- <a href="https://docs.dna-evolutions.com/overview_docs/special_features/Special_Features.html" target="_blank">Overview of special features</a>
- Our official repository 	- <a href="https://public.repo.dna-evolutions.com" target="_blank">public.repo.dna-evolutions.com</a>
- Our official JavaDocs 		- <a href="https://public.javadoc.dna-evolutions.com" target="_blank">public.javadoc.dna-evolutions.com</a>
- Our YouTube channel - <a href="https://www.youtube.com/channel/UCzfZjJLp5Rrk7U2UKsOf8Fw" target="_blank">DNA Tutorials</a>
- Documentation - <a href="https://docs.dna-evolutions.com/rest/touroptimizer/rest_touroptimizer.html" target="_blank">DNA's RESTful Spring-TourOptimizer in Docker </a>
- Our DockerHub channel - <a href="https://hub.docker.com/u/dnaevolutions" target="_blank">DNA DockerHub</a>
- Our LinkedIn channel - <a href="https://www.linkedin.com/company/dna-evolutions/" target="_blank">DNA LinkedIn</a>


If you need any help, please contact us via our company website <a href="https://www.dna-evolutions.com" target="_blank">www.dna-evolutions.com</a> or write an email to <a href="mailto:info@dna-evolutions.com">info@dna-evolutions.com</a>.


---

## Why using JOpt products from DNA Evolutions?
Originally, JOpt is a flexible routing optimization-engine written in Java, allowing to solve tour-optimization problems that are highly restricted, for example, regarding time windows, skills, and even mandatory constraints can be applied.

Click, to open our video:

<a href="https://www.youtube.com/watch?v=U4mDQGnZGZs" target="_blank"><img src="https://dna-evolutions.com/wp-content/uploads/2021/02/joptIntrox169_small.png" width="500"
title="Introduction Video for DNA's JOpt" alt="Introduction Video for DNA's JOpt"></a>

---
