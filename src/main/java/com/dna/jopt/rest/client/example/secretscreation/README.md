# Secrets-Creator Example

For running an example, it is usually necessary to provide a key. Either your API-Key and/or your JOpt-Tour-Optimizer Key. Please get in touch with us in case you are interested in purchasing an API Key, or please reach out to us for getting a limited API-Test-Key.

## Creating a secrets file

Most examples are using a  ` SecretManager ` instance loading a predefined JSON secrets file. This file is **not** part of this repository. You can open  `SecretCreatorExampleHelper.java`  (within this package) and fill out the required information. Running this example will create a file called  `secrets.json`  in the folder `src/main/resources/secrets/`  that looks like this:

```xml
{ 
	"azure" : "YOUR_DNA_AZURE_API_KEY",
	"joptlic": "YOUR_JOPT_JSON_LIC"
}
```

The file contains a `Map<String, String>` in `JSON` format.

## Public TourOptimizer License
We do not provide any public API-Keys for using our Azure-Endpoints. However, hosting a local Rest-TourOptimizer instance is possible. You can use the limited (max 15 elements) public TourOptimizer License that can be found as a variable in `com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator` named `PUBLIC_JSON_LICENSE`. For more information please refer to the TourOptimizer Examples.

## Modifying the secrets path

Please open `com.dna.jopt.rest.client.util.secrets.SecretsManager.java` and modify the `DEFAULT_SECRETS_PATH` variable. Or use a different constructor for the `SecretsManager` instance when exploring the examples.

For example:

```xml
	SecretsManager m = new SecretsManager(); 
```
 will create an instance of `SecretsManager` expecting the secrets file to be stored in `DEFAULT_SECRETS_PATH`.
 
 Whereas:
 
 ```xml
	String secretFilePath = "/anyFolder/mySecrets.json";
	SecretsManager m = new SecretsManager(secretFilePath); 
```

 will create an instance of `SecretsManager` expecting the secrets file to be stored in `/anyFolder/mySecrets.json`.
 
 (Please also check out the other constructors of the `SecretsManager.java`.)
 
 
## Recommendation - Keep your secrets secret
Please be careful not to share your API Key or your JOpt-TourOptimizer Key. By default, a snapshot always strips any of your keys, and therefore, sharing snapshots is possible.