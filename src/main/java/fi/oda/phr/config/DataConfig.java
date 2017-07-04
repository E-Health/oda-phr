package fi.oda.phr.config;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.data")
public class DataConfig {
    public static final String SET_DESCRIPTION = "description";

    public static final String SET_FILE = "file";

    public static final String SET_ORDER = "order";

    public static final String SET_INJECTOR_PROPS = "injector_properties";

    public static final String SET_INJECTOR_CLASS = "injector";

    public static final String INJECTOR_PROP_USE_UPDATE = "use_update";

    private Map<String, Map<String, String>> resources;

    public Map<String, Map<String, String>> getResources() {
        return resources;
    }
    
    public void setResources(Map<String, Map<String, String>> resources) {
        this.resources = resources;
    }

    public List<String> getDatasetKeysByPriority() {
        return resources.entrySet()
                .stream()
                .sorted(
                        new Comparator<Map.Entry<String, Map<String, String>>>() {

                            @Override
                            public int compare(Entry<String, Map<String, String>> obj1, Entry<String, Map<String, String>> obj2) {
                                int priority1 = Integer.valueOf(obj1.getValue().get(DataConfig.SET_ORDER));
                                int priority2 = Integer.valueOf(obj2.getValue().get(DataConfig.SET_ORDER));
                                if (priority1 == priority2) {
                                    return 0;
                                }
                                else if (priority1 < priority2) {
                                    return -1;
                                }
                                return 1;
                            }
                        })
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    public Map<String, String> getDatasetProperties(String setKey) {
        return resources.get(setKey);
    }

    public String getDatasetFile(String setKey) {
        return resources.get(setKey).get(DataConfig.SET_FILE);
    }

    public String getInjectorClass(String setKey) {
        return resources.get(setKey).get(DataConfig.SET_INJECTOR_CLASS);
    }

    public Integer getOrder(String setKey) {
        return Integer.valueOf(resources.get(setKey).get(DataConfig.SET_ORDER));
    }

}
