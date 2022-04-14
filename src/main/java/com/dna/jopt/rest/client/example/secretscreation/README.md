# Secrets-Creator Example

For running an example, it is usually necessary to provide a key. Either your API-Key and/or your JOpt-Tour-Optimizer Key. Please get in touch with us in case you are interested in purchasing an API Key, or please reach out to us for getting a limited API-Test-Key.

## Creating a secrets file

Most examples are using a  ` SecretManager ` instance loading a predefined JSON secrets file. This file is **not** part of this repository. You can open  `SecretCreatorExampleHelper.java`  (within this package) and fill out the required information. Running this example will create a file called  `secrets.json`  in the folder `/secrets/`  that looks like this:

```xml
{ 
	"azure" : "YOUR_DNA_AZURE_API_KEY",
	"joptlic": "YOUR_JOPT_JSON_LIC"
}
```

Of course you can also manually place a `secrets.json` file inside the `/secrets/` folder.

The file contains a `Map<String, String>` in `JSON` format.

## Public TourOptimizer License
We do not provide any public API-Keys for using our Azure-Endpoints. However, hosting a local Rest-TourOptimizer instance is possible. You can use the limited (max 15 elements) public TourOptimizer License that can be found as a variable in `com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator` named `PUBLIC_JSON_LICENSE`. For more information please refer to the TourOptimizer Examples.

## Creating an instance of SecretsManager with a Map<String,String>

You can directly pass a `Map<String, String>` where the key is the title of your provider and the value is the secret password.
 
 ```xml
Map<String, String> secretsmap = new HashMap<>();
secretsmap.put("azure", "YOUR_DNA_PROVIDED_API_KEY");
secretsmap.put("joptlic", "YOUR_DNA_PROVIDED_TOUROPTIMIZER_KEY");
	
SecretsManager smanager = new SecretsManager(secretsmap);
```

 
## Recommendation - Keep your secrets secret
Please be careful not to share your API Key or your JOpt-TourOptimizer Key. By default, a snapshot always strips any of your keys, and therefore, sharing snapshots is possible.