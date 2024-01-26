# TourOptimizer Examples - Fire and Forget


## Good advice before you start
Please visit <a href="https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer/blob/main/TourOptimizerWithDatabase.md">https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer/blob/main/TourOptimizerWithDatabase.md</a> before you start and learn how to set up TourOptimizer with Mongo. In additon, you will learn how to use the magic keyword **hash:**.


## Order of execution (no data encryption)
After you successfully started a TourOptimizer communicating with a Mongo Database, you can test the following scenario:

1. Write an optimization into the Database
2. Search your stored optimization
3. Read/Load your stored optimization


## Write an Optimization to Mongo
Execute the example **TourOptimizerFireAndForgetWriteExample.java**. By default the creator string is **"TEST_CREATOR"** and the id for the optimization is **"MY_OPTIMIZATION"**. 

## Search an Optimization
Execute the example **TourOptimizerFireAndForgetSearchInDatabaseExample.java**. If you modifed the creator string or used the magic hash key word, you have to adjust the creator string.

You should get something back like:

```xml
[ {
  "creator" : "TEST_CREATOR",
  "ident" : "MY_OPTIMIZATION",
  "type" : "OptimizationConfig<JSONConfig>",
  "contentType" : "application/x-bzip2",
  "id" : "65b3ab6a5292b22226f525d2"
} ]
```

## Load an Optimization
You will have to provide the following data to load an un-encrypted optimization:
- The creator string - in our previous examples we used **"TEST_CREATOR"**
- The mongo id of the database optimization entry. In our case it is the **"id"** key from our serach result = **"65b3ab6a5292b22226f525d2"**
in the **TourOptimizerFireAndForgetLoadFromDatabaseExample.java** file before executing it.
