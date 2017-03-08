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
        final List<DataInjector> result = new ArrayList<DataInjector>();
        //Bundle of patients
        result.add(new BundleInjector("datasets/patient-bundle.json",
                "responses/patient-bundle-response.json"));
        //Practitioner
        result.add(new ResourceInjector("datasets/practitioner1.json",
                "responses/practitioner1-response.json", true));

        //Testi Anna with temperature observations
        result.add(new ResourceInjector("datasets/PATIENT1/patient.json",
                "responses/patient1-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT1/person.json",
                "responses/person1-response.json", true));
        int i;
        for (i = 1; i <= 7; i++) {
            result.add(new ResourceInjector("datasets/PATIENT1/observation" + i + ".json",
                    "responses/patient1-observation" + i + "-response.json", true));
        }
        //TODO add additional data sets here. Items will be inserted in the order they are put in the list.
        return result;
    }

}
