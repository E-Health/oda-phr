# ODA PHR

This project contains a server for persisting and manipulating FHIR resources. 
The server provides CRUD operations with REST interfaces according to the FHIR 
specification. In development mode data is stored in a Derby database.

## Building

    ./gradlew clean build
    
This creates oda-phr.jar under build/libs.

## Running

Starting in dstu3 mode:

    java -jar oda-phr.jar -Dspring.profiles.active=dstu3

Starting in dstu2 mode (deprecated):

    java -jar oda-phr.jar -Dspring.profiles.active=dstu2

## Database
ODA PHR uses an in-memory database, which is cleared every time the server is 
rebooted. A disk-based database can be used by changing the configuration:

```yml
spring:
    datasource:
        url: jdbc:derby:directory:build/jpaserver_derby_files;create=true   
```

## Data sets        
Data can be injected in to the database when the server is started. This 
behavior is configured with the "datasets" bean in Dstu2Config and Dstu3Config.

The bean should return a list of objects that implement the DataInjector 
interface. If no data needs to be injected, the bean should return an empty 
list. DataInjector has a single inject-method, which takes a FHIR client as an 
argument. Classes that implement DataInjector use the provided FHIR client for 
sending data to the server. ODA PHR provides two injectors: 
- BundleInjector
- ResourceInjector

BundleInjector is used for persisting a bundle of FHIR resources as a 
transaction. ResourceInjector is used for storing a single FHIR resource. After 
the server has started, it creates a client for itself and calls all configured 
injectors with the client. Injectors are called in the order they are provided 
in the "datasets" bean list. The order of the execution can be important when 
one resource contains references to another one. 

When storing data to the server, the server returns a response message with 
generated resource ids. The response is stored in a file and the location of 
the file is configured with injector constructor arguments. The ids in the 
response can used for building test environments. For example, a test script 
might loop through a list of ids and query the server for the data. The 
constructor of ResourceInjector also allows to choose between update and create 
operations. When using a create operation, the server assigns an id for the 
stored resource. When using an update operation, it is possible to define an id 
in the FHIR resource. This user-specified id has to have at least one 
non-numeric character. Custom ids are necessary when FHIR resources contain 
references to other FHIR resources. 



## Available data sets

| Data set | Description | Source |
| ---- | ------- | ------- |
| [oda-patients.json](src/main/resources/oda-patients.json) | A bundle of 225 patients with names and birthdates | [HL7 test data](https://www.hl7.org/FHIR/2017Jan/downloads.html) |
| [testi-anna.json](src/main/resources/testi-anna.json) | A resource for a single patient, Testi Anna | [Osuuspankki test user](https://support.signicat.com/display/S2/Finnish+Tupas+test+info)|

Injectors could also be used for storing profiles to the server.


