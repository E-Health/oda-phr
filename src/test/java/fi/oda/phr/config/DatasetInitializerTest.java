package fi.oda.phr.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.junit.*;

import ca.uhn.fhir.rest.client.*;
import fi.oda.phr.dataset.*;
public class DatasetInitializerTest {
    private IGenericClient fhirClient;

    private DatasetInitializer datasetInitializer;

    private DataConfig dataConfig;

    private List<Map<String, String>> injectors;

    private final String resourceInjector = "fi.oda.phr.dataset.ResourceInjector";

    private final String bundleInjector = "fi.oda.phr.dataset.BundleInjector";

    private final String foobarInjector = "fi.oda.phr.dataset.Foobar";

    @Before
    public void setUp() {
        fhirClient = mock(GenericClient.class);
        injectors = new ArrayList<Map<String, String>>();

    }
    @Test
    public void parseValidInjectors() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        dataConfig = new DataConfig();
        Map<String, String> validInjectors = new HashMap<String, String>();
        validInjectors.put("file", "datafile.json");
        validInjectors.put("injector", resourceInjector);
        injectors.add(validInjectors);
        validInjectors = new HashMap<String, String>();
        validInjectors.put("file", "datafile2.json");
        validInjectors.put("injector", bundleInjector);
        injectors.add(validInjectors);
        dataConfig.setResources(injectors);
        datasetInitializer = new DatasetInitializer(fhirClient, dataConfig, "true");
        List<DataInjector> sets = datasetInitializer.parseDatasets(dataConfig);
        assertThat(sets.size(), equalTo(2));
        assertThat(sets, hasItem(isA(ResourceInjector.class)));
        assertThat(sets, hasItem(isA(BundleInjector.class)));
    }

    @Test(expected = ClassNotFoundException.class)
    public void parseInvalidInjector() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        dataConfig = new DataConfig();
        Map<String, String> validInjectors = new HashMap<String, String>();
        validInjectors.put("file", "datafile.json");
        validInjectors.put("injector", foobarInjector);
        injectors.add(validInjectors);
        validInjectors = new HashMap<String, String>();
        validInjectors.put("file", "datafile2.json");
        validInjectors.put("injector", bundleInjector);
        injectors.add(validInjectors);
        dataConfig.setResources(injectors);
        datasetInitializer = new DatasetInitializer(fhirClient, dataConfig, "true");
        datasetInitializer.parseDatasets(dataConfig);
    }

}
