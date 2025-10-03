# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWOZVYSnfoccKQCLAwwAIIgQKAM4TMAE0HAARsAkoYMhZkzowUAJ4TcRNAHMYABgB0ATkzGoEAK7YAxABYAzAA4ATFZAxnZGMACzAFBAdVACUUYyQJHUEkCDRMRFRSAFoAPhZqSgAuGABtAAUAeTIAFQBdGAB6BxUoAB00AG8AIibKNGAAWxROgs6YToAaMdwpAHdoGWHRibGUfuAkBEWxgF9MCnzYHLy2cSKoWPiwSgAKGLiEyhiAR0iEgEo91nYYI4EhUXEUiKxhQYAAqs1rj0oH1Bh8-iIxJIJD9cuoCgAxJBoGQwCGUBEwBS6GDQ2EcBTnYAAazxzRgMyQYGCpOa5JgwAQVJkJJQAA9LtIEQDkT8jvsTigivioAjPtRvplxaxCjBhFyUMAeVUaegAKJ88TYAgpeU0Q5HdLaIquMzuNpdQZSYAg4ZjPVQexQIpkgaqTncklCanoTq7dAyTDCpFSMW5CXsIpoBwIBBmxW5aOAiQFEBUq4yqHNBHw+Qi2NHdHCGS4mVyhPiVEwLPI3P5lDCBzM67ALvBEtRssxlGVhQFau4zvM+tfRtK+Mq70cvszg5x+DIa0wbxmMwO7psv2jIpLSadXvMqoQENoY9jHZaHGaOyOFzQXgggIYuB6vhwYQwAAMhAcSpFaWQLgcRRlJUtQNCoUjJGgDq+oMkzTBIcxQDIuwSpQTYNlKMAICB2LXMBoHPK8YAfIRTYtkCMAguCkKoSgpb-MOTboliOJ0gS8hEiSbGYJSmq0jKDJMiybFqBAkhoAA5Mw-KXIOnHZuuhFJimaZ0fOxyqmCaDnPcVznDIMoGkaJqpHhFq5OBYA2na+5OhILpDCeHpekUoAgI4pCMsyTFIDQaCsr0fpyQpykwKpCRho+kYMSOyoKqc-GyvI6ZzpmQ7Zm2mpXFOwQ9iu8gcYimmjuONZqhVQi5aoBn2X5jXAGa+EGU5RQ7nuHQHlFgx3qeYwXsEV43qND4Rs+9hOM4dgoOgARBKEy2rRRjhYE5BGLtBfB6oBepVHqdT1AhEhIW0E1TegXWHJBkpFCRoHkSBO1UZINHNfRBWtkxoKleVl7XugVXliOaJjrxk4dUJy5gzeolUrSd3gxFMjyRISkqQKCTqdVoqtbORHJqmf2k1BeImSgAXGGgSAAF4oDIpX3Wg1koMaSGPeuvUwLa9qDe5nlup0PnQO1yOrdjsX45cSVzalWlk0UpWrpK-0aa2MgoAgoIdn2oP9pVRNQ9xY4xP0EA0A1045XNtgLS4FkBNg2K0oBlwwAA4n60h7el5rQX7p0XcYfq3X2nP8yHibEZcAdOqbnO0WTOvE4xzEgxjN6Q1xtUwHDDtm0IiP5w9Yk0kjk2YzFuNxQlWCq9TL0wBTemZ+3Rl0wzTOs+zseY9zvOmvZAubhgLki46yLi95nrS3XnON3j8UE2AytPm3CeZZrOX6flus56CKeSNchc1TDRR+6C-t+j7hN789icX8-WD6SHqof2pk89Wns5GA7RkrzVfM4GQAB2KwZgUBmACHqdwcAfwADZ4DtkfoMGAaQgH7RpjBaokdo59BHjeB0UdBgADk-ThifC7CBugDYkRmGtJAIQwBMNTBAVhAApCA2IsGqGcOEUA1JcEZGYD-JcZQwRwXqJQlAMdZbIQ6NgBAwAmFQDgBAEiUBJiKIAJJ8DoZGBhi07CaLYRwyxJJECalgMAbA6jCBJBSDg4Oz1VTFCOidM6F11BgKAA

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
