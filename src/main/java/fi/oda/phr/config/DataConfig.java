package fi.oda.phr.config;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class DataConfig {
    public static final String SET_DESCRIPTION = "description";

    public static final String SET_FILE = "file";

    public static final String SET_PRIORITY = "priority";

    public static final String SET_INJECTOR_PROPS = "injector_properties";

    public static final String SET_INJECTOR_CLASS = "injector";

    public static final String INJECTOR_PROP_USE_UPDATE = "use_update";

    private Map<String, Map<String, String>> data;

    public Map<String, Map<String, String>> getData() {
        return data;
    }
    
    public void setData(Map<String, Map<String, String>> data) {
        this.data = data;
    }

    public List<String> getDatasetKeysByPriority() {
        return data.entrySet()
                .stream()
                .sorted(
                        new Comparator<Map.Entry<String, Map<String, String>>>() {

                            @Override
                            public int compare(Entry<String, Map<String, String>> obj1, Entry<String, Map<String, String>> obj2) {
                                int priority1 = Integer.valueOf(obj1.getValue().get(DataConfig.SET_PRIORITY));
                                int priority2 = Integer.valueOf(obj2.getValue().get(DataConfig.SET_PRIORITY));
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
        return data.get(setKey);
    }

    public String getDatasetFile(String setKey) {
        return data.get(setKey).get(DataConfig.SET_FILE);
    }

    public String getInjectorClass(String setKey) {
        return data.get(setKey).get(DataConfig.SET_INJECTOR_CLASS);
    }

    public Integer getPriority(String setKey) {
        return Integer.valueOf(data.get(setKey).get(DataConfig.SET_PRIORITY));
    }

}
