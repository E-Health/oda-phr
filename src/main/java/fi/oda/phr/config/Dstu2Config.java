package fi.oda.phr.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu2;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu2;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import fi.oda.phr.dataset.DataInjector;
import fi.oda.phr.profiles.Dstu2Profile;

@Dstu2Profile
@Configuration
@Import(BaseJavaConfigDstu2.class)
public class Dstu2Config {

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.DSTU2, "baseDstu2", 60000);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor subscriptionSecurityInterceptor() {
        return new SubscriptionsRequireManualActivationInterceptorDstu2();
    }

    @Bean
    public List<DataInjector> datasets() {
        //TODO: Add DSTU2 datasets if needed
        return Collections.emptyList();
    }
}
