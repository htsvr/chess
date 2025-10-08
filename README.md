# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Sequence Diagram](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWOZVYSnfoccKQCLAwwAIIgQKAM4TMAE0HAARsAkoYMhZkzowUAJ4TcRNAHMYABgB0ATkzGoEAK7YAxABYAzAA4ATFZAxnZGMACzAFBAdVACUUYyQJHUEkCDRMRFRSAFoAPhZqSgAuGABtAAUAeTIAFQBdGAB6BxUoAB00AG8AIibKNGAAWxROgs6YToAaMdwpAHdoGWHRibGUfuAkBEWxgF9MBSgUYABrGABJNCpgZBkYA4BHSITMCnzYTJz4ZG0i1zMzNvaY0GUmAxiGRU6AFEoPYoEUlDd7o8wKNdugZM9WJQYB8XmxxEUDnEEpQABQxYlgSgxB6SMAASkx1HYONyAiEonEUiKYLAAFVmqSelA+oNGeyRGJJBJWWoFAUAGJINA3AWUCUwBS6GDC0UcfaHE5q2AzJBgYI65p6mBXA7AGTalAAD3iYGkEs50pxuNY7CKxolTPxqneuTxhRgwgQdodVWO6EhTvE2AIKSD2NDnwyYB+ZncAKB0tB4LG0NhRVthwdMCER3QqK0KswHqlUm9Yd9BJgaAcCAQQZZHxbXIkBRAdqpxqFzQl4vknrbH3UBWEMlVM-kA-EsuH0rHE5Qwgc5tJwGPwVnzfnrZlS-lq5uR-Ngbxg59rwr55fWLeH3S3xgbw-gBborQGIYYAhMZJk6M9zSqCA6zQUYoM6NEm1sewnGcaBeDBAIFTgSE+DgYQYAAGQgOJUn-LIOw-EoKmqOp6hUKRkjQEDdXA4YxmgqZlAkOYoAWVDdgNY4zguK4kERFBaSecNf1yWicxgX5-g6QsQTBXioRhaB4XtW55ORBt0XTN56ODIoECo5VSUo6iaWRRlX23IdrxHHkUH5QVuLFK8ORvWVlyVFUYADeRNW1AKOF3Rd3xsyKNyEPY7SNMDBhgaAYGmIT5hgeIirQEBoAOcAtxDJKI3OMqYRQcBjT1cooFKQThJkRNk1TVIlPbLMAN+ABGAtOmBCRiz0stDJ1Ppz2gJAAC8UAWGB0IxdyQzZLy93HQ4qSfYJTy-eQ52CkdQvvNdI1OtKtoGpTP2fTd+szVSiiAzSujilC+OWWDzwQpC-qWDbNDsRwXDsFB0ACIJQhhuGnMcLBVNlJ6Sj4SFyMhKpIRYtiJA4to4OCYH0EsjHOxQWyqNRxz6ePFy6Tcmmd127kYF5I6TvgxD0HOyVLrvRVlUfO7gBim0gYF1IJJOMmKbQNQIEkNAAHJmGdV0guFr1My2ooez7KrHp-f00Aqqi0GW1ajuV7qUBTDiqfer4MFzUaOnGotdIhGa4TmsnFpWhZwYS28kr9W6Xvu9nPIuvcZBQBBfMPc8+YvM69YXW9cmXGJ+ggGhY+z+PXgGj7AOAjpwcwqHnAOG5nGwZUTnI10YAAcXA6R0ZqoPim7vGWOMcDSdlpC3esmPkASXvgSz5W2eZDydqTrmeczpW5aFvOrqKcKJbjqWtRl-np4Vi-ybl1X1a1mAdaeSPzbX2nu17fsHsNi3IqtxqNs7YyAdnLJ2Ls0xvT-B7NSI0QITSmgHAyQcHDzXNKHVaaFGwYlfr-d+RQjrfnfhzTeo5ua+UXpIUk+8Qqix7r5Hu4FO4v05lHWeXZKHMKwD-QeRROG6ygSpGBn1a6Ak6OPYEvFihdAkSgU4fBeLDW8O4VwMEZjBDNCgZqPERj8U6OEUARxtGDC2ADWRAA5cCui0IwBqPXSG2EZAAHYrBmBQGYAIkJ3BwCIgANngAeRh2U0gwOpgxMolRagNFkZPS+6AAQWPAuJDKUlLjXBMgpbhP4q7CPUqI7Sk1-almQUZOSmTzJNjeuwj++1BAoEoaSRJgxJi7yQqvYMJD9Zb18rzVpgtc60ILvKGAx8y4anPn0+WKTJn3wkJrbWLoWGkLfslE238E68P-tbYwtsw6gKQuA3qbtoHZi9vAv2JZ9LlmDgtKAwCsEWVwdHLshDXoJw3l0shvIGmyNODIGhIshlH3FkElA4ztS-IxNfSFNpoxVkdIst0AyRbPI-pQohHTUXGxQDMP51DsHIoNliz+psf4fLzkUWpVIGmUIBQbIFkYbrouipQ2QGgeHWQjJC45QjTk12+mMX5CiihKJUetAlDdsK6FTnZGY8MkAhDANKvsEA5UACkIDKlBQEAxIAjghOzGEmgQ9Sh8iifUGJaDb5IQBNgBAwBpVQDgBAOyUBJhCuSYaVJMkynIh5YNT2eSBW+x0pcwOpSMlmXFRZKpeRkoACtNVoAaRq5ULMEjtMHOSm8PkwC9Knv03BDLRmvKENLSZ6UvUzJkGrOZj9n5YCedU42X8zZ4ONZbbZuz7YFrQIc12giA2wLMN7LoCCilXNmqgkOdyw4PKbE2uNMdS3ADbdm7y5CwA-PAn8uli5i0guZWW8+3LoXgVmfMp+iLCWJWbSS9ZxD20Rkjv2yB2T3Z8t+PmLSIbClhpKbCmM2paz1mjQu1hKyY5HtXdfdNVIbhlVdUVGUlZ7TAfjH1DZnKg5RiA3GA5SZnZHMHdXL9Y1x3-uuah6sIHkJgc2u+3lAEvoAnsVhaGUAHXysVXYLjiBDiwGANgO1hAkgpBgAa7QRqIzFGxrjfGLF1AzyXV2EAac8D4rJTASOlL1NQHxUWuURQ+Cp3TjASExrdDmmVMYf11cWN1wJUAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
