package fi.oda.phr.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.IRestfulClientFactory;
import fi.oda.phr.JpaServer;
import fi.oda.phr.dataset.DataInjector;

/**
 * Configuration for injecting test data sets to the server
 *
 */
@Component
public class DatasetConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final JpaServer server;

    private final String serverAddress;

    private final List<DataInjector> datasets;

    private final FhirConfig config;

    public DatasetConfig(JpaServer server, FhirConfig config, @Value("${server.port}") String port,
            @Value("${server.contextPath}") String contextPath, List<DataInjector> datasets) {
        this.config = config;
        this.serverAddress = "http://localhost:" + port + "/" + contextPath + "/" + config.path;
        this.server = server;
        this.datasets = datasets;
    }

    /**
     * Injects data to the PHR after the application has started.
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        final IRestfulClientFactory factory = server.getFhirContext().getRestfulClientFactory();
        factory.setConnectTimeout(config.timeout);
        factory.setConnectionRequestTimeout(config.timeout);
        factory.setSocketTimeout(config.timeout);
        final IGenericClient client = factory.newGenericClient(serverAddress);

        datasets.forEach((v) -> {
            v.inject(client);
        });

    }

}
