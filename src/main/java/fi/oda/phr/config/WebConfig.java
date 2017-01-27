package fi.oda.phr.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import fi.oda.phr.JpaServer;

@Configuration
@ComponentScan("ca.uhn.fhir.to")
public class WebConfig {

  private final JpaServer jpaFhirServer;

  public WebConfig(JpaServer jpaFhirServer) {
    this.jpaFhirServer = jpaFhirServer;
  }

  @Bean
  public ServletRegistrationBean servletRegistrationBean() {
    final ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(jpaFhirServer, "/baseDstu2/*");
    servletRegistrationBean.setLoadOnStartup(1);
    return servletRegistrationBean;
  }
}
