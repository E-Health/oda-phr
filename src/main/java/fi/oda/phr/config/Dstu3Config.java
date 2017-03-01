package fi.oda.phr.config;

import java.util.ArrayList;
import java.util.List;

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
import fi.oda.phr.dataset.ResourceInjector;
import fi.oda.phr.profiles.Dstu3Profile;

@Dstu3Profile
@Configuration
@Import(BaseJavaConfigDstu3.class)
public class Dstu3Config {

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.DSTU3, "baseDstu3", 60000);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor subscriptionSecurityInterceptor() {
        return new SubscriptionsRequireManualActivationInterceptorDstu3();
    }

    @Bean
    public List<DataInjector> datasets() {
        //TODO: Make data sets configurable in the yaml file?
        final List<DataInjector> result = new ArrayList<DataInjector>();

        result.add(new BundleInjector("oda-patients.json",
                "responses/oda-patients-response.json"));
        result.add(new ResourceInjector("testi-anna.json",
                "responses/testi-anna-response.json", false));
        result.add(new ResourceInjector("demo-anna.json",
                "responses/demo-anna-response.json", true)); //Use UPDATE (PUT) to be able to define FHIR resource id
        result.add(new ResourceInjector("demo-anna-careplan.json",
                "responses/demo-anna-response.json", false));
        //TODO add additional data sets here. Items will be inserted in the order they are put in the list.
        return result;
    }

}
