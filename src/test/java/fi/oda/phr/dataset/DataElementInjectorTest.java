package fi.oda.phr.dataset;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class DataElementInjectorTest {

    @Autowired
    DataElementInjector dataElementInjector = new DataElementInjector();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void inject() throws Exception {
        final FhirContext ctx = FhirContext.forDstu3();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        final IParser parser;
        parser = ctx.newJsonParser();

        parser.setPrettyPrint(true);
        String sourceFile = "FHIRForms/fhirform-example-qu-1.json";
        IBaseResource resource;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            resource = parser.parseResource(reader);

            // beapen: If resource is a questionnaire, apply dataelement injector
            if (resource.getClass() == Questionnaire.class) {
                Questionnaire questionnaire = (Questionnaire) resource;
                resource = dataElementInjector.inject(questionnaire);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read data file", e);
        }
        Questionnaire q2 = (Questionnaire) resource;
        Assert.assertNotNull(resource);
        System.out.print(q2.getIdElement());
        System.out.print(q2.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-deMap"));


    }

}