# Kela PHR mock

## Dokumentaatio
Tämän projektin FHIR-palvelinta voidaan käyttää Kela PHR sandboxin sijaan. Projektin sisältämä palvelin
tarjoaa rajapinnat FHIR-resurssien tallentamiseen ja lukemiseen.  

Projektin kääntäminen:
mvn install

Asentaminen:
Kääntäminen luo paketin target/kela-phr-mock.war, joka voidaan ladata sovelluspalvelimeen. 

Käyttö:
Hallintakäyttöliittymä löytyy sovelluksen juuresta [palvelimen osoite]:[portti]/kela-phr-mock

FHIR-rajapintojen polku: [palvelimen osoite]:[portti]/kela-phr-mock/baseDstu2/ 

Asetukset:
Kela PHR mock tallentaa FHIR-resurssit Apache Derby -tietokantaan. Kannan sijainti tulee antaa sovelluspalvelimelle
JVM:n parametrina -DmockPHRPath=[tietokannan hakemisto]. Esimerkki: "-DmockPHRPath=C:/work/kela"

Jos hakemistoa ei ole olemassa levyllä, niin Kela PHR mock luo sen.