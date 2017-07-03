package fi.oda.phr.config;

import java.lang.reflect.*;
import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.client.*;
import fi.oda.phr.JpaServer;
import fi.oda.phr.dataset.*;

/**
 * Initializes the database with datasets
 *
 */
@Component
public class DatasetInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final JpaServer server;

    private final String serverAddress;

    private final List<DataInjector> datasets;

    private final FhirConfig config;

    private final Logger log = LoggerFactory.getLogger(DatasetInitializer.class);

    private final String defaultInjectorClass = ResourceInjector.class.getName();

    private final String useUpdateKey = "use_update";

    private final String useUpdateDefault = "true";

    private boolean runDataOnStart = false;
    public DatasetInitializer(JpaServer server, FhirConfig config, @Value("${server.port}") String port,
            @Value("${server.contextPath}") String contextPath, DataConfig dataConfig,
            @Value("${app.data.feed_on_start}") String runDataOnStart)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.runDataOnStart = Boolean.valueOf(runDataOnStart);
        this.config = config;
        this.serverAddress = "http://localhost:" + port + "/" + contextPath + "/" + config.path;
        this.server = server;
        datasets = new ArrayList<DataInjector>();
        parseDatasets(dataConfig);
    }

    private void parseDatasets(DataConfig data) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (String key : data.getDatasetKeysByPriority()) {
            log.info("Creating dataset injector for " + key);
            String injectorName = data.getInjectorClass(key);
            Map<String, String> properties = data.getDatasetProperties(key);
            
            if (injectorName == null) {
                injectorName = defaultInjectorClass;
            }
            if (!properties.containsKey(useUpdateKey)) {
                properties.put(useUpdateKey, useUpdateDefault);
            }
            Class<?> injectorClass = Class.forName(injectorName, true, DataInjector.class.getClassLoader());
            Constructor<?> injectorConstructor = injectorClass.getConstructor(String.class, Map.class);
            datasets.add((DataInjector) injectorConstructor.newInstance(key, properties));
        }
        log.info("Done loading datasets");
    }
    /**
     * Injects data to the PHR after the application has started.
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!runDataOnStart) {
            return;
        }
        feedData();
    }

    public void feedData() {
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
