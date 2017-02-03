package fi.oda.phr.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import fi.oda.phr.JpaServer;
import fi.oda.phr.profiles.Dstu3Profile;

@Dstu3Profile
@Configuration
@ComponentScan("ca.uhn.fhir.to")
public class WebConfigDstu3 {

    private final JpaServer jpaFhirServer;

    public WebConfigDstu3(JpaServer jpaFhirServer) {
        this.jpaFhirServer = jpaFhirServer;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        final ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(jpaFhirServer, "/baseDstu3/*");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }
}
