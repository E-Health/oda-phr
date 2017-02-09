package fi.oda.phr.config;

import ca.uhn.fhir.to.TesterConfig;
import fi.oda.phr.JpaServer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
				.withName("Local Tester")
			.addServer()
				.withId("hapi")
				.withFhirVersion(fhirConfig.versionEnum)
				.withBaseUrl("http://fhirtest.uhn.ca/" + fhirConfig.path)
				.withName("Public HAPI Test Server");
		return retVal;
	}
}
