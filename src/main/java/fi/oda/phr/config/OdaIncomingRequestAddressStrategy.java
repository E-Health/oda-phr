package fi.oda.phr.config;

import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.rest.server.IncomingRequestAddressStrategy;
import fi.oda.phr.profiles.DevProfile;

@Configuration
@DevProfile
public class OdaIncomingRequestAddressStrategy extends IncomingRequestAddressStrategy {

}
