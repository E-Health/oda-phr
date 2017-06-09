package fi.oda.phr.config;

import java.util.*;

import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu3;
import ca.uhn.fhir.rest.server.interceptor.*;
import fi.oda.phr.dataset.*;
import fi.oda.phr.profiles.Dstu3Profile;
import fi.oda.phr.validation.OdaValidatingInterceptor;
@Dstu3Profile
@Configuration
@Import(BaseJavaConfigDstu3.class)
public class Dstu3Config {

    @Bean
    public RequestValidatingInterceptor validationInterceptor(){
        //PrePopulatedValidationSupport validationSupport = new PrePopulatedValidationSupport();
        //TODO: Populate validationSupport with ODA profiles (addCodeSystem, addStructureDefinition, addValueSet)
        final List<Class<? extends IBaseResource>> ignoreList = new ArrayList<>();
        ignoreList.add(CarePlan.class);
        ignoreList.add(Bundle.class);
        return new OdaValidatingInterceptor(Optional.empty(), ignoreList);
    }

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
        final List<DataInjector> result = new ArrayList<>();

        // THIS HAS TO BE FIRST !! ID MUST BE 1
        result.add(new ResourceInjector("datasets/Hengitystietulehdusoireiden_itsearvio_fixed_id1.json",
                "responses/Hengitystietulehdusoireiden_itsearvio_fixed-response_id1.json", false));

        result.add(new ResourceInjector("datasets/questionnaires/Oikeus_henkilokohtaiseen_apuun.json",
                "responses/Oikeus_henkilokohtaiseen_apuun-response.json", false));

        result.add(new BundleInjector("datasets/patient-bundle.json",
                "responses/patient-bundle-response.json"));
        result.add(new ResourceInjector("datasets/organization1.json",
                "responses/organization1-response.json", true));
        result.add(new ResourceInjector("datasets/practitioner1.json",
                "responses/practitioner1-response.json", true));
        result.add(new ResourceInjector("datasets/person-practitioner1.json",
                "responses/person-practitioner1-response.json", true));
        result.add(new ResourceInjector("datasets/practitioner2.json",
                "responses/practitioner2-response.json", true));
        result.add(new ResourceInjector("datasets/person-practitioner2.json",
                "responses/person-practitioner2-response.json", true));

        result.add(new ResourceInjector("datasets/PATIENT1/patient.json",
                "responses/patient1-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT1/person.json",
                "responses/person1-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT1/careteam-authorization.json",
                "responses/patient1-careteam-authorization-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT1/careteam-care.json",
                "responses/patient1-careteam-care-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT1/episodeofcare.json",
                "responses/patient1-episodeofcare-response.json", true));

        result.add(new ResourceInjector("datasets/PATIENT1/careplan.json",
                "responses/patient1-careplan-response.json", true));

        int i;
        for (i = 1; i <= 7; i++) {
            result.add(new ResourceInjector("datasets/PATIENT1/observation" + i + ".json",
                    "responses/patient1-observation" + i + "-response.json", true));
        }

        result.add(new ResourceInjector("datasets/PATIENT2/patient.json",
                "responses/patient2-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/person.json",
                "responses/person2-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/observation.json",
                "responses/patient2-observation-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/episodeofcare.json",
                "responses/patient2-episodeofcare-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/careteam-authorization.json",
                "responses/patient2-careteam-authorization-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/careplan.json",
                "responses/patient2-careplan-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/communication.json",
                "responses/patient2-communication-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/careteam-care.json",
               "responses/patient2-careteam-care-response.json", true));
        result.add(new ResourceInjector("datasets/PATIENT2/episodeofcare.json",
                "responses/patient2-episodeofcare-response.json", true));

        //TODO add additional data sets here. Items will be inserted in the order they are put in the list.
        return result;
    }

}
