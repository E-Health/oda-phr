package fi.oda.phr.config;

import java.util.*;

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

    private List<Map<String, String>> resources;

    public List<Map<String, String>> getResources() {
        return resources;
    }

    public void setResources(List<Map<String, String>> resources) {
        this.resources = resources;
    }
    
    /*    public void setResources(List<Map<String, String>> resources) {
        this.resources = resources;
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
    }*/
}
