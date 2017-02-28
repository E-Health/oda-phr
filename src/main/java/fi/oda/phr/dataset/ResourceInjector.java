package fi.oda.phr.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Reads a FHIR resource bundle (transaction) from the classpath and sends it to the server
 *
 */
public class ResourceInjector implements DataInjector {

    private final Logger log = LoggerFactory.getLogger(ResourceInjector.class);

    //Bundle is read from this file (Must be available in the classpath)
    public final String sourceFile;

    //Response is written to this file
    private final String responseFile;

    private final boolean useUpdate;

    public ResourceInjector(String sourceFile, String responseFile, boolean useUpdate) {
        this.useUpdate = useUpdate;
        this.sourceFile = sourceFile;
        this.responseFile = responseFile;
    }

    @Override
    public void inject(IGenericClient client) {
        if (log.isDebugEnabled()) {

        }
        final FhirContext ctx = FhirContext.forDstu3();
        final IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);
        IBaseResource resource;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream()))) {
            resource = parser.parseResource(reader);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        final MethodOutcome result;
        if (useUpdate) {
            result = client.update().resource(resource).execute();
        }
        else {
            result = client.create().resource(resource).prettyPrint().encodedJson().execute();
        }
        if (log.isDebugEnabled()) {

        }
        try {

            Files.createDirectories(Paths.get(responseFile).getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(responseFile))) {
                parser.encodeResourceToWriter(result.getOperationOutcome(), writer);
            }
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        if (log.isDebugEnabled()) {

        }
    }

}
