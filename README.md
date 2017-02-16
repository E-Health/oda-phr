# ODA PHR

## Documentation
This project contains a server for persisting and manipulating FHIR resources. The server provides CRUD operations with REST interfaces according to the FHIR specification. Data is stored in a Derby database.

Starting in dstu3 mode:
java -jar oda-phr.jar -Dspring.profiles.active=dstu3

Starting in dstu2 mode:
java -jar oda-phr.jar -Dspring.profiles.active=dstu2

The default settings in the application.yml file define an in-memory database, which is cleared every time the server is rebooted. A disk-based database can be used by changing the configuration as follows:
spring:
    datasource:
        url: jdbc:derby:directory:build/jpaserver_derby_files;create=true   
        
The server injects test data into the database every time it is started. This behavior can be changed and new data sets can be added by editing the "datasets" bean in Dstu2Config and Dstu3Config.