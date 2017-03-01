package fi.oda.phr.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.IRestfulClientFactory;
import ca.uhn.fhir.to.TesterConfig;
import ca.uhn.fhir.util.ITestingUiClientFactory;
import fi.oda.phr.JpaServer;

@Configuration
public class FhirTesterConfig {

    private final JpaServer jpaFhirServer;

    public FhirTesterConfig(JpaServer jpaFhirServer) {
        this.jpaFhirServer = jpaFhirServer;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(FhirConfig fhirConfig) {
        final ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(jpaFhirServer, "/" + fhirConfig.path + "/*");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

    @Bean
    public TesterConfig testerConfig(FhirConfig fhirConfig) {

        final TesterConfig retVal = new TesterConfig();
        retVal
                .addServer()
                .withId("home")
                .withFhirVersion(fhirConfig.versionEnum)
                .withBaseUrl("${serverBase}/" + fhirConfig.path)
                .withName("Local Tester");

        retVal.setClientFactory(new ITestingUiClientFactory() {

            @Override
            public IGenericClient newClient(FhirContext theFhirContext, HttpServletRequest theRequest, String theServerBaseUrl) {
                final IRestfulClientFactory factory = theFhirContext.getRestfulClientFactory();
                factory.setConnectionRequestTimeout(fhirConfig.timeout);
                factory.setConnectTimeout(fhirConfig.timeout);
                factory.setSocketTimeout(fhirConfig.timeout);
                return factory.newGenericClient(theServerBaseUrl);
            }
        });
        return retVal;
    }
}
