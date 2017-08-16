package fi.oda.phr.config;

import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.client.IGenericClient;
import fi.oda.phr.dataset.*;

/**
 * Initializes the database with datasets
 *
 */
@Component
public class DatasetInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger log = LoggerFactory.getLogger(DatasetInitializer.class);

    private final String defaultInjectorClass = ResourceInjector.class.getName();

    private final String useUpdateKey = "use_update";

    private final String useUpdateDefault = "true";

    private final DataConfig dataConfig;

    private final IGenericClient fhirClient;

    private boolean runDataOnStart = false;

    public DatasetInitializer(IGenericClient fhirClient, DataConfig dataConfig,
            @Value("${app.data.feed_on_start}") String runDataOnStart) {
        this.runDataOnStart = Boolean.valueOf(runDataOnStart);
        this.fhirClient = fhirClient;
        this.dataConfig = dataConfig;
    }

    public List<DataInjector> parseDatasets(
            DataConfig dataConfig) {
        List<DataInjector> datasets = new ArrayList<DataInjector>();
        for (Map<String, String> dataset : dataConfig.getResources()) {
            String injectorName = dataset.get(DataConfig.SET_INJECTOR_CLASS);
            injectorName = ObjectUtils.defaultIfNull(dataset.get(DataConfig.SET_INJECTOR_CLASS), defaultInjectorClass);
            dataset.putIfAbsent(useUpdateKey, useUpdateDefault);
            try {
                Class<?> injectorClass = Class.forName(injectorName, true, DataInjector.class.getClassLoader());
                Constructor<?> injectorConstructor = injectorClass.getConstructor(Map.class);
                datasets.add((DataInjector) injectorConstructor.newInstance(dataset));
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Done loading datasets");
        return datasets;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (runDataOnStart) {
                feedData(parseDatasets(dataConfig), fhirClient);
        }
    }

    public void feedData(List<DataInjector> datasets, IGenericClient fhirClient) {
        datasets.forEach((v) -> {
            v.inject(fhirClient);
        });

    }

}
