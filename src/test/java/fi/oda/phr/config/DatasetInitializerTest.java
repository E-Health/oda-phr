package fi.oda.phr.config;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.*;

import org.junit.*;
import org.junit.rules.ExpectedException;

import ca.uhn.fhir.rest.client.*;
import fi.oda.phr.dataset.*;
public class DatasetInitializerTest {
    private IGenericClient fhirClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DatasetInitializer datasetInitializer;

    private DataConfig dataConfig;

    private List<Map<String, String>> injectors;

    private final String resourceInjector = "fi.oda.phr.dataset.ResourceInjector";

    private final String bundleInjector = "fi.oda.phr.dataset.BundleInjector";

    private final String foobarInjector = "fi.oda.phr.dataset.Foobar";

    private Map<String, String> bundleInjectorConfig;

    private Map<String, String> resourceInjectorConfig;

    private Map<String, String> foobarInjectorConfig;

    @Before
    public void setUp() {
        fhirClient = mock(GenericClient.class);
        resourceInjectorConfig = new HashMap<String, String>();
        bundleInjectorConfig = new HashMap<String, String>();
        foobarInjectorConfig = new HashMap<String, String>();

        resourceInjectorConfig.put("file", "datafile.json");
        resourceInjectorConfig.put("injector", resourceInjector);

        bundleInjectorConfig.put("file", "datafile2.json");
        bundleInjectorConfig.put("injector", bundleInjector);

        foobarInjectorConfig.put("file", "datafile.json");
        foobarInjectorConfig.put("injector", foobarInjector);

    }
    @Test
    public void parseValidInjectors() {
        dataConfig = new DataConfig();
        injectors = asList(bundleInjectorConfig, resourceInjectorConfig);
        dataConfig.setResources(injectors);
        datasetInitializer = new DatasetInitializer(fhirClient, dataConfig, "true");
        List<DataInjector> sets = datasetInitializer.parseDatasets(dataConfig);
        assertThat(sets.size(), equalTo(2));
        assertThat(sets, hasItem(isA(ResourceInjector.class)));
        assertThat(sets, hasItem(isA(BundleInjector.class)));
    }

    @Test
    public void parseInvalidInjector() {        
        dataConfig = new DataConfig();
        injectors = asList(foobarInjectorConfig);
        dataConfig.setResources(injectors);
        thrown.expect(RuntimeException.class);
        thrown.expectCause(isA(ClassNotFoundException.class));
        thrown.expectMessage("java.lang.ClassNotFoundException: fi.oda.phr.dataset.Foobar");
        datasetInitializer = new DatasetInitializer(fhirClient, dataConfig, "true");
        datasetInitializer.parseDatasets(dataConfig);
        
    }

}
