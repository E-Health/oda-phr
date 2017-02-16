package fi.oda.phr.config;

import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu3;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import fi.oda.phr.dataset.BundleInjector;
import fi.oda.phr.dataset.DataInjector;
import fi.oda.phr.profiles.Dstu3Profile;

@Dstu3Profile
@Configuration
@Import(BaseJavaConfigDstu3.class)
public class Dstu3Config {

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.DSTU3, "baseDstu3");
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor subscriptionSecurityInterceptor() {
        return new SubscriptionsRequireManualActivationInterceptorDstu3();
    }

    @Bean
    public SortedMap<Integer, DataInjector> datasets() {
        //TODO: Make data sets configurable in the yaml file?
        final SortedMap<Integer, DataInjector> result = new TreeMap<Integer, DataInjector>();

        result.put(0, new BundleInjector("oda-patients.json",
                "responses/oda-patients-response.json"));
        //TODO insert additional data sets here. Use the precedence value (map key) to control order of data insertion.
        //Smaller number means that the data set is injected sooner in the chain.
        return result;
    }

}
