# Fullstack Examples

Fullstack examples combine calls to different services. We provide access to:

- JOpt-TourOptimizer based on JOpt-Core (available as a local Container and via Azure)
- JOpt-GeoCoder based on JOpt-Core and Pelias (available via Azure)
- JOpt-RoutePlanner based on JOpt-Core and Valhalla (available via Azure)

## Good advice before you start
By default, you cannot access our azure-endpoints without a working API Key. Please contact us in case you are interested in purchasing an API Key or please reach out to us for getting a limited API-Test-Key.

## JOpt-GeoCoder
Forward-/Reverse geocode Addresses or Positions.

## JOpt-RoutePlanner
Find distances and driving times between points. Either as turn-by-turn result optionally providing driving instructions and route shapes, or as matrix request for multiple routings.

## JOpt-TourOptimizer
Optimize a problem consisting of Nodes, Resources, and optionally externally provided connections. In contrast to our other services, we allow to host your own JOpt-TourOptimizer locally.


## Fullstack Examples - Overview

- Package `coderoute`: The first step: Take addresses and geocode them to positions with longitude and latitude. Second step: Create a connections matrix.
- Package `coderouteoptimize`: Like `coderoute`, but with additional optimization.
- Package `helper`: Contains the Rest-Caller used by the examples.
