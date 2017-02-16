package fi.oda.phr.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.hl7.fhir.dstu3.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Reads a FHIR resource bundle (transaction) from the classpath and sends it to the server
 *
 */
public class BundleInjector implements DataInjector {

    private final Logger log = LoggerFactory.getLogger(BundleInjector.class);

    //Bundle is read from this file (Must be available in the classpath)
    public final String sourceFile;

    //Response is written to this file
    private final String responseFile;

    public BundleInjector(String sourceFile, String responseFile) {

        this.sourceFile = sourceFile;
        this.responseFile = responseFile;
    }

    @Override
    public void inject(IGenericClient client) {
        final FhirContext ctx = FhirContext.forDstu3();
        final IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);
        Bundle bundle;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream()))) {
            bundle = parser.parseResource(Bundle.class, reader);
        }
        catch (final IOException e) {
            log.error("Unable to read data file", e);
            return;
        }
        final Bundle result = client.transaction().withBundle(bundle).execute();
        try {
            Files.createDirectories(Paths.get(responseFile).getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(responseFile))) {
                parser.encodeResourceToWriter(result, writer);
            }
        }
        catch (final IOException e) {
            log.warn("Unable to write response to " + responseFile, e);
        }
    }

}
