package fi.oda.phr.config;

import ca.uhn.fhir.context.FhirVersionEnum;
import fi.oda.phr.profiles.Dstu2Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Dstu2Profile
@Configuration
public class Dstu2Config {

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.DSTU2, "baseDstu2");
    }
}
