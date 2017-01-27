package fi.oda.phr;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.BaseJpaSystemProvider;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;

@Component
public class JpaServer extends RestfulServer {

  private static final long serialVersionUID = 1L;

  public JpaServer(List<IResourceProvider> resourceProviders,
      BaseJpaSystemProvider<?, ?> systemProvider,
      IFhirSystemDao<Bundle, MetaDt> dao,
      DaoConfig daoConfig,
      DatabaseBackedPagingProvider dbBackedPagingProvider,
      Collection<IServerInterceptor> interceptorBeans,
      @Value("${app.fhirVersion}") String fhirVersion) {

    final FhirContext context = new FhirContext(FhirVersionEnum.valueOf(fhirVersion));

    setFhirContext(context);

    setResourceProviders(resourceProviders);
    setPlainProviders(systemProvider);
    final JpaConformanceProviderDstu2 confProvider = new JpaConformanceProviderDstu2(this, dao, daoConfig);
    confProvider.setImplementationDescription("Example Server");
    setServerConformanceProvider(confProvider);
    setPagingProvider(dbBackedPagingProvider);
    for (final IServerInterceptor interceptor : interceptorBeans) {
      this.registerInterceptor(interceptor);
    }

  }

  @Override
  protected void initialize() throws ServletException {
    super.initialize();

    /*
     * We want to support FHIR DSTU2 format. This means that the server
     * will use the DSTU2 bundle format and other DSTU2 encoding changes.
     *
     * If you want to use DSTU1 instead, change the following line, and
     * change the 2 occurrences of dstu2 in web.xml to dstu1
     */

    /*
     * Enable ETag Support (this is already the default)
     */
    setETagSupport(ETagSupportEnum.ENABLED);

    /*
     * This server tries to dynamically generate narratives
     */
    final FhirContext ctx = getFhirContext();
    ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

    /*
     * Default to JSON and pretty printing
     */
    setDefaultPrettyPrint(true);
    setDefaultResponseEncoding(EncodingEnum.JSON);

    /*
     * Enable CORS
     */
    final CorsConfiguration config = new CorsConfiguration();
    final CorsInterceptor corsInterceptor = new CorsInterceptor(config);
    config.addAllowedHeader("Origin");
    config.addAllowedHeader("Accept");
    config.addAllowedHeader("X-Requested-With");
    config.addAllowedHeader("Content-Type");
    config.addAllowedHeader("Access-Control-Request-Method");
    config.addAllowedHeader("Access-Control-Request-Headers");
    config.addAllowedOrigin("*");
    config.addExposedHeader("Location");
    config.addExposedHeader("Content-Location");
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    registerInterceptor(corsInterceptor);

  }

}
