# RoutePlanner Examples

RoutePlanner examples call our JOpt-RoutePlanner services.

## Good advice before you start
By default, you cannot access our azure-endpoints without a working API Key. Please contact us in case you are interested in purchasing an API Key or please reach out to us for getting a limited API-Test-Key.

## JOpt-RoutePlanner
Find distances and driving times between points. Either as turn-by-turn result optionally providing driving instructions and route shapes or as matrix request for multiple routings.

## RoutePlanner Examples - Overview

- Package `fullmatrix`: Transform a list of source and targeted positions into a list of connections representing every possible connection between each point (representing the full n x n matrix).
- Package `smartmatrix`: Transform a list of resources and a list of nodes into a list of most likely connections. This can reduce the number of elements by up to 90% by utilising information like opening hours of nodes, working hours of Resources, and constraints.
- Package `turnbyturn`: Creates a turn-by-turn request and saved the shape of the routing as a kml file.
- Package `helper`: Contains the Rest-Caller used by the examples.
