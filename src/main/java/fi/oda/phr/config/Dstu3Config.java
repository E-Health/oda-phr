package fi.oda.phr.config;

import ca.uhn.fhir.context.FhirVersionEnum;
import fi.oda.phr.profiles.Dstu3Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Dstu3Profile
@Configuration
public class Dstu3Config {

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.DSTU3, "baseDstu3");
    }
}
