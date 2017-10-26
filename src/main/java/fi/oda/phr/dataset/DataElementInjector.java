package fi.oda.phr.dataset;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import org.hl7.fhir.dstu3.model.DataElement;
import org.hl7.fhir.dstu3.model.Element;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class DataElementInjector {
    private final Logger log = LoggerFactory.getLogger(ResourceInjector.class);

    @Value("${spring.application.uri}")
    private String uri;

    @Value("${spring.application.demap}")
    private String demap = "http://hl7.org/fhir/StructureDefinition/questionnaire-deMap";

    /**
     * Injects the dataElement into a questionnaire if it contains demap extension
     */
    public Questionnaire inject(Questionnaire questionnaire) {

        log.info("Processing: {}", questionnaire.getId());
        List<Extension> extensions = new ArrayList();
        if (questionnaire.getExtensionsByUrl(demap) != null)
            extensions = questionnaire.getExtensionsByUrl(demap);
        // If no extensions
        if (extensions.isEmpty())
            return questionnaire;
        else {
            for (Extension extension : extensions) {
                String extension_value = extension.getValue().toString();
                // If demap extension value has uri, process it
                if (extension_value.contains("uri")) {
                    log.info("Reading Extension: {}", extension_value);
                    // Replace the uri with local path
                    String sourceFile = extension_value.replace(uri, "DataElements/");
                    // The segment below is from ResourceInjector.java
                    log.info("About to inject: {}", sourceFile);
                    final FhirContext ctx = FhirContext.forDstu3();
                    ctx.setParserErrorHandler(new StrictErrorHandler());
                    final IParser parser;
                    if (sourceFile.toLowerCase().endsWith(".xml")) {
                        parser = ctx.newXmlParser();
                    } else {
                        parser = ctx.newJsonParser();
                    }
                    parser.setPrettyPrint(true);
                    IBaseResource resource;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream(),
                                    java.nio.charset.StandardCharsets.UTF_8))) {
                        resource = parser.parseResource(reader);
                        // In this case the resource is a DataElement
                        DataElement dataElement = (DataElement) resource;
                        // Convert elements in the dataElement resource to QuestionnaireItemComponent and add
                        // them to the questionnaire.
                        for (Element element : dataElement.getElement()) {
                            Questionnaire.QuestionnaireItemComponent item = (Questionnaire.QuestionnaireItemComponent) element;
                            questionnaire.addItem(item);
                        }

                    } catch (final IOException e) {
                        throw new RuntimeException("Unable to read data file", e);
                    }
                }
            }
            return questionnaire;
        }
    }
}


