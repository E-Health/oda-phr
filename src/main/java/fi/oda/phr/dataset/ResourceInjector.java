package fi.oda.phr.dataset;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.*;
import org.springframework.core.io.ClassPathResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.*;
import ca.uhn.fhir.rest.client.IGenericClient;
import fi.oda.phr.config.DataConfig;

/**
 * Reads a FHIR resource bundle from the classpath and sends it to the server
 *
 */
public class ResourceInjector implements DataInjector {

    private final Logger log = LoggerFactory.getLogger(ResourceInjector.class);

    //Resource is read from this file (Must be available in the classpath)
    public final String sourceFile;

    private final boolean useUpdate;

    public ResourceInjector(Map<String, String> parameters) {
        this.sourceFile = parameters.get(DataConfig.SET_FILE);
        useUpdate = Boolean.parseBoolean(parameters.get(DataConfig.INJECTOR_PROP_USE_UPDATE));
    }


    @Override
    public void inject(IGenericClient client) {
        log.info("About to inject: {}", sourceFile);
        final FhirContext ctx = FhirContext.forDstu3();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        final IParser parser;
        if (sourceFile.toLowerCase().endsWith(".xml")) {
            parser = ctx.newXmlParser();
        }
        else {
            parser = ctx.newJsonParser();
        }
        parser.setPrettyPrint(true);

        IBaseResource resource;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            resource = parser.parseResource(reader);
        }
        catch (final IOException e) {
            throw new RuntimeException("Unable to read data file", e);
        }
        if (useUpdate) {
            client.update().resource(resource).execute();
        }
        else {
            client.create().resource(resource).prettyPrint().encodedJson().execute();
        }
        log.info("Finished injecting: {}", sourceFile);
    }

}
