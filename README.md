# ODA PHR

## Dokumentaatio
Tämän projektin FHIR-palvelinta voidaan käyttää Kela PHR sandboxin sijaan. Projektin sisältämä palvelin
tarjoaa rajapinnat FHIR-resurssien tallentamiseen ja lukemiseen.  

Palvelimen käynnistäminen dstu3-tilassa:
java -jar oda-phr.jar -Dspring.profiles.active=dstu3

Palvelimen käynnistäminen dstu2-tilassa:
java -jar oda-phr.jar -Dspring.profiles.active=dstu2
