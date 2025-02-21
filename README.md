# Import Script
With this script you can validate and import your database.csv file into a [JSON Server stable version (v0.17.4)](https://github.com/typicode/json-server/tree/v0).

To run the script you can [build](#build-the-project) the project yourself or [use](#run-the-jar) the provided .jar file.

## Run the jar

To run the jar need at least [jdk 21](https://www.oracle.com/java/technologies/downloads/#jdk21-windows) and a [JSON Server stable version (v0.17.4)](https://github.com/typicode/json-server/tree/v0).

After starting the JSON Server you can start the script. You have to pass the Path to the csv file and the URL of your Json Server as an argument to the program.

Example for the command:

```java -jar import-script.jar database.csv http://localhost:3000/customers```

## Build the project

To build the project you can use the following commands in the project's directory:

```./gradlew shadowjar``` this will create a fatjar 

```./gradlew build``` this will create a jar

This will output a runnable in the build directory