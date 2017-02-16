package fi.oda.phr.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.client.IGenericClient;
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

    public DatasetConfig(JpaServer server, FhirConfig config, @Value("${server.port}") String port,
            @Value("${server.contextPath}") String contextPath, List<DataInjector> datasets) {
        this.serverAddress = "http://localhost:" + port + "/" + contextPath + "/" + config.path;
        this.server = server;
        this.datasets = datasets;
    }

    /**
     * Injects data to the PHR after the application has started.
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        final IGenericClient client =
                server.getFhirContext().newRestfulGenericClient(serverAddress);

        datasets.forEach((v) -> {
            v.inject(client);
        });

    }

}
