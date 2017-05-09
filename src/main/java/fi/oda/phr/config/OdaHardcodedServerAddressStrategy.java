package fi.oda.phr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import fi.oda.phr.profiles.TestProfile;

@Configuration
@TestProfile
public class OdaHardcodedServerAddressStrategy extends HardcodedServerAddressStrategy {

    public OdaHardcodedServerAddressStrategy(@Value("${app.fhir-server.url}") String theValue) {
        super(theValue);
    }
}
