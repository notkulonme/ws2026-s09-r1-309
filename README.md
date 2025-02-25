# Dashboard API

To run the API you can [build](#build-the-project) the project yourself or [use](#run-the-jar) the provided .jar file.

[showcase video](https://youtu.be/3Rdu2szfEsE)

Import Script is in the other branch.

## API endpoints

Everything is returned in an object where the key is the api endpoint in camel case.

> /customers/count

Returns the total customer count.

> /customers/avg-age

Returns the average age of customers.

> /customers/most-frequent-purchase-category
 
Returns the most preferred purchase category.

> /customers/sum-of-purchase

Returns the total purchase value.

> /customers/avg-order-value

Returns the average order value.

> /customers/purchase-frequency

Returns the average purchase frequency.

> /customers/gender-dist

Returns the genders each count in a matrix where the first value is the gender's name and the second is the count of it in the database.

> /customers/membership-dist

Returns the membership level counts the same matrix format as `/customers/gender-dist`.

> /customers/categories

Returns categories each count the same matrix format as `/customers/gender-dist`.

> /customers/top-spenders

Returns the top 10 spenders in the same matrix format as `/customers/gender-dist`

> /customers/trends

Returns the new customers count/year in the same matrix format as `/customers/gender-dist`

> /customers

Returns the whole database.

## Run the jar

To run the jar need at least [jdk 21](https://www.oracle.com/java/technologies/downloads/#jdk21-windows) and a [JSON Server stable version (v0.17.4)](https://github.com/typicode/json-server/tree/v0).

After starting the JSON Server you can start the API. The program doesn't take any argument. You need to run the API in the same directory with the assets' directory or configure it manually. The jar file is in the build directory.

Example for the command:

```bash 
  java -jar dashboard-api-all.jar
```

## Build the project

To build the project you can use the following commands in the project's directory:

```./gradlew shadowjar``` this will create a fatjar

```./gradlew build``` this will create a jar

This will output a runnable in the build directory

## Configure manually

For custom configuration you need a yaml file. Run the jar with `-config=application.yaml` flag.

Basic yaml layout:
```yaml
ktor:
    application:
        modules:
            - hu.notkulonme.ApplicationKt.module
    deployment:
        port: 8080
api:
    database_url : "http://localhost:3000/customers" #rewrite this for another database endpoint
    assets : "assets/"                               #rewrite this for another assets directory path
```
