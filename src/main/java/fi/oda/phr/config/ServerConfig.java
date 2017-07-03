package fi.oda.phr.config;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;

import org.hl7.fhir.dstu3.hapi.validation.PrePopulatedValidationSupport;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import ca.uhn.fhir.context.*;
import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu3;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.interceptor.*;
import fi.oda.phr.validation.OdaValidatingInterceptor;
@Configuration
@Import(BaseJavaConfigDstu3.class)
public class ServerConfig {

    @Bean
    public RequestValidatingInterceptor validationInterceptor(){
        PrePopulatedValidationSupport validationSupport = new PrePopulatedValidationSupport();
        FhirContext ctx = FhirContext.forDstu3();
        IParser parser = ctx.newXmlParser();
        validationSupport.addStructureDefinition(loadStructureDefinition("profiles/ODA-Communication.structuredefinition.xml", parser));
        //TODO: Populate validationSupport with ODA profiles (addCodeSystem, addStructureDefinition, addValueSet)
        final List<Class<? extends IBaseResource>> ignoreList = new ArrayList<>();
        ignoreList.add(CarePlan.class);
        ignoreList.add(Bundle.class);
        return new OdaValidatingInterceptor(Optional.of(validationSupport), ignoreList);
    }

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.DSTU3, "baseDstu3", 60000);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor subscriptionSecurityInterceptor() {
        return new SubscriptionsRequireManualActivationInterceptorDstu3();
    }

    private StructureDefinition loadStructureDefinition(String file, IParser parser) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(file).toString()).getInputStream(), Charset.forName("UTF-8")))) {
            return (StructureDefinition) parser.parseResource(reader);
        }
        catch (final IOException e) {
            throw new RuntimeException("Unable to read profile file", e);
        }
    }

}
