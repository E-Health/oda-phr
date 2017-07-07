# ODA PHR

This project contains a server for persisting and manipulating FHIR resources. The server provides CRUD operations with REST interfaces according to the FHIR  specification.

[![Dependency Status](https://www.versioneye.com/user/projects/58ef3c3673eac40052fd19ad/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58ef3c3673eac40052fd19ad)

[![Build Status](https://travis-ci.org/omahoito/oda-phr.svg?branch=master)](https://travis-ci.org/omahoito/oda-phr)


## Building

    ./gradlew clean build

This creates oda-phr.jar under build/libs.

## Running

The server can be started with the following command:

    java -jar oda-phr.jar
    
Hapi server web interface: http://localhost:6083/phr/


## Initial data        
The database of the server can be initialized with data when the server starts. Alternatively, data initialization can be triggered manually with a REST interface. This is configured in the program settings in application.yml.

```yml
app:
    data:
        feed_on_start: true
        resources:
                - file: datasets/practitioner1.json
                  injector: fi.oda.phr.dataset.ResourceInjector
                  use_update: true
                - file: datasets/practitioner2.json
                - file: datasets/practitioner3.json
                       
```

If feed_on_start is set to true, data will be inserted when the server is started. Data insertion can also be performed by sending an HTTP POST with an empty body to the following url:
[oda-phr-base]/data/init

Inserted data is configured under app.data.resources. Data resources are given as a list of maps. A mandatory file parameter has to be specified, which determines the location of the data file. The file has be available on the classpath and it can be in json or xml format. Data resources are inserted in the order they are given in the list. Two optional parameters are also supported: use_update and injector. The injector parameter defines the class of the data injector. The class has to implement the DataInjector interface. If no value is specified for injector, a default of fi.oda.phr.dataset.ResourceInjector is used. If use_update is set to false, the data resource is inserted with a FHIR create operation. Otherwise an update operation is used. If no value is specified for use_update, a default of true is used. When using an update operation, an id has to be defined in the FHIR resource. This user-specified id has to have at least one non-numeric character. Custom ids are necessary when FHIR resources contain references to other FHIR resources. 

ODA PHR provides two injector classes:
- BundleInjector
- ResourceInjector

BundleInjector is used for persisting a bundle of FHIR resources as a transaction. ResourceInjector is used for storing a single FHIR resource. 


## Validating FHIR resources against the server

ODA PHR can test whether a given FHIR resource would be a valid input for the server. Validation is performed by sending an HTTP POST to the server. The path of the request should point to the extended operation $validate under the resource type. For example:     
https://oda.medidemo.fi/phr/baseDstu3/Patient/$validate

When basic general validation is sufficient (e.g. validate that the resource conforms to the specification), the request body is the FHIR resource that needs to be validated. However, it should be noted that a FHIR resource might be a valid candidate for one operation, but an invalid one for another one. For example, the resource might conform to the specification, but would not be a valid input for a create operation due to uniqueness constraints. If more strict validation is needed, the request body should be a Parameters resource. In this case, it is possible to supply a profile and a validation mode in addition to the actual FHIR resource.

| Parameter name | Parameter type | Description | Mandatory |
| ---- | ------- | ------- | ------- |
| resource | FHIR resource | The resource that should be validated | * |
| mode | valueCode | Type of operation to validate (create, update or delete) |  |
| profile | valueUri | Profile to validate against |  |

Example request body:
```json
{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "resource",
      "resource": {
          "resourceType": "Patient",
          "id" : "PATIENT1",
          "communication": [
            {
              "language": {
                 "coding": [
                   {
                     "system": "urn:ietf:bcp:47",
                     "code": "fi-FI",
                     "display": "Finnish"
                   }
                 ]
              },
              "preferred": true
            }
          ]
      }  
    },
    {
      "name": "mode",
      "valueCode" : "create"
    },
    {
      "name" : "profile",
      "valueUri" : "http://someprofile"
    }

  ]
}
```

Example curl command for validating a FHIR resource: 
```
curl -X POST https://oda.medidemo.fi/phr/baseDstu3/Patient/\$validate \
     --data @patient.json \
     --header "Content-Type: application/fhir+json"
```

If the validation succeeds, the server will return 200 OK. A failed validation will result in 400 Bad Request.
