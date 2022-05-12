# TourOptimizer Examples

TourOptimizer examples call our JOpt-TourOptimizer services.

## Good advice before you start
By default, you cannot access our azure-endpoints without a working API Key. Please get in touch with us in case you are interested in purchasing an API Key, or please reach out to us for getting a limited API-Test-Key.


## JOpt-TourOptimizer
Optimize a problem consisting of Nodes, Resources, and optionally externally provided connections. In contrast to our other services, we allow you to host your JOpt-TourOptimizer locally.

## How to locally start TourOptimizer
JOpt-TourOptimizer is hosted on <a href="https://hub.docker.com/r/dnaevolutions/jopt_touroptimizer" target="_blank">Docker Hub</a>. In a first step, you can pull the image and start a container in your local docker environment, using, for example, <a href="https://docs.docker.com/desktop/" target="_blank">Docker Desktop</a>.

Setting up JOpt-TourOptimizer in your Docker environment:


**1) Running a container (the image will be pulled if absent):**

```xml
docker run -d --rm  --name myJOptTourOptimizer -e SPRING_PROFILES_ACTIVE="cors" -p 8081:8081  dnaevolutions/jopt_touroptimizer
```

Activating the profile "cors" will allow doing REST calls from the same local host from another application.

(If desired, please adjust <a href="https://docs.docker.com/engine/reference/run/" target="_blank">docker run argument</a> to your needs)


**2) Open and check:** <a href="http://localhost:8081" target="_blank">http://localhost:8081</a>

...and you should see the Swagger-Interface.

(Please refer to <a href="https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer" target="_blank">Docker-Rest-TourOptimzer-Repository</a> for more information)

**Note: In the future, we will only allow using Docker-REST-TourOptimizer if you have purchased an XL-License. For testing purposes, however, you can still use the public license.**


## TourOptimizer Examples - Overview

- Package `attachtostreams`: Optimize a list of Nodes and Resources. Connections are **not** provided; moreover, they will be created using haversine calculations on the server-side. Further, we attach a custom stream-subscription via a BiConsumer function for receiving information like current optimization progress.
- Package `fireandforget`: Optimize and only receive a started signal. The result will be saved to a mongo database.
- Package `optimize`: Optimize a list of Nodes and Resources. Connections are **not** provided; moreover, they will be created using haversine calculations on the server-side.
- Package `optimizefromloadedparts`: Optimize a list of Nodes and Resources. The Resource positons, Node positions and the connections are loaded from predefined JSON definitions.
- Package `optimizefromsnapshot`: Optimize a list of Nodes and Resources. The complete optimization definition is loaded from a single snapshot. This snapshot is compatible with our core Java and core .NET JOpt version.
- Package `optimizelocal`:  Optimize a list of Nodes and Resources on your local machine. A docker environment is required.
- Package `scenarios`: Collection of scenario examples motivated by <a href="https://github.com/DNA-Evolutions/Java-TourOptimizer-Examples" target="_blank">Java-TourOptimizer-Examples</a>
- Package `helper`: Contains the Rest-Caller used by the examples.
