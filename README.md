# ODA PHR

## Description
This project contains a server for persisting and manipulating FHIR resources. The server provides CRUD operations with REST interfaces according to the FHIR specification. Data is stored in a Derby database.

Starting in dstu3 mode:
java -jar oda-phr.jar -Dspring.profiles.active=dstu3

Starting in dstu2 mode:
java -jar oda-phr.jar -Dspring.profiles.active=dstu2

## Database
ODA PHR uses an in-memory database, which is cleared every time the server is rebooted. A disk-based database can be used by changing the configuration:
spring:
    datasource:
        url: jdbc:derby:directory:build/jpaserver_derby_files;create=true   

## Data sets        
Data can be injected in to the database when the server is started. This behavior is configured with the "datasets" bean in Dstu2Config and Dstu3Config.

The bean should return a list of objects that implement the DataInjector interface. If no data needs to be injected, the bean should return an empty list. DataInjector has a single inject-method, which takes a FHIR client as an argument. Classes that implement DataInjector use the provided FHIR client for sending data to the server. ODA PHR provides two injectors: 
- BundleInjector
- ResourceInjector

BundleInjector is used for persisting a bundle of FHIR resources as a transaction. ResourceInjector is used for storing a single FHIR resource. After the server has started, it creates a client for itself and calls all injectors with the client. Injectors are called in the order they are provided in the "datasets" bean list. The order of the execution can be important when one resource contains references to another one.

The constructors of BundleInjector and ResourceInjector take a response file as the second argument. This file is used for storing the response from the server. The response contain identifiers that the server generated for the resources. If the database is configured to be stored on the disk, the returned identifiers can used for constructing test environments. For example, a test script might loop through a list of identifiers and query the server for the data.

## Available data sets

| Data set | Description | Source |
| ---- | ------- | ------- |
| [oda-patients.json](src/main/resources/oda-patients.json) | A bundle of 225 patients with names and birthdates | [HL7 test data](https://www.hl7.org/FHIR/2017Jan/downloads.html) |
| [testi-anna.json](src/main/resources/testi-anna.json) | A resource for a single patient, Testi Anna | [Osuuspankki test user](https://support.signicat.com/display/S2/Finnish+Tupas+test+info)|

Injectors could also be used for storing profiles to the server.


