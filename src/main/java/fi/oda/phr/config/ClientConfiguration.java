package fi.oda.phr.config;

import java.util.*;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class ClientConfiguration {
    private List<String> urls = new ArrayList<String>();

    public List<String> getLocalUrls() {
        return urls;
    }

    public void setLocalUrls(List<String> urls) {
        this.urls = urls;
    }

}
