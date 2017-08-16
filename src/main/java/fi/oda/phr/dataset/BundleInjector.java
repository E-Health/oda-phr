package fi.oda.phr.dataset;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;

import org.hl7.fhir.dstu3.model.Bundle;
import org.slf4j.*;
import org.springframework.core.io.ClassPathResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.*;
import ca.uhn.fhir.rest.client.IGenericClient;
import fi.oda.phr.config.DataConfig;

/**
 * Reads a FHIR resource bundle (transaction) from the classpath and sends it to the server
 *
 */
public class BundleInjector implements DataInjector {

    private final Logger log = LoggerFactory.getLogger(BundleInjector.class);

    //Bundle is read from this file (Must be available in the classpath)
    public final String sourceFile;

    public BundleInjector(Map<String, String> parameters) {
        this.sourceFile = parameters.get(DataConfig.SET_FILE);
    }

    @Override
    public void inject(IGenericClient client) {
        log.info("About to inject: {} ", sourceFile);
        final FhirContext ctx = FhirContext.forDstu3();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        final IParser parser = ctx.newJsonParser();        
        parser.setPrettyPrint(true);
        Bundle bundle;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            bundle = parser.parseResource(Bundle.class, reader);
        }
        catch (final IOException e) {
            throw new RuntimeException("Unable to read file", e);
        }
        client.transaction().withBundle(bundle).execute();
        log.info("Finished injecting: {}", sourceFile);
    }

}
