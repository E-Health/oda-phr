package fi.oda.phr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.BaseJpaSystemProvider;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.server.*;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import fi.oda.phr.config.FhirConfig;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Meta;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class JpaServer extends RestfulServer {

    private static final long serialVersionUID = 1L;
    private Optional<RequestValidatingInterceptor> validationInterceptor;
    
    @SuppressWarnings("unchecked")
    public JpaServer(List<IResourceProvider> resourceProviders,
            BaseJpaSystemProvider<?, ?> systemProvider,
            IFhirSystemDao<?, ?> dao,
            DaoConfig daoConfig,
            DatabaseBackedPagingProvider dbBackedPagingProvider,
            Collection<IServerInterceptor> interceptorBeans,
            FhirConfig fhirConfig,
            Optional<RequestValidatingInterceptor> validationInterceptor,
            IServerAddressStrategy addressStrategy) {
        this.validationInterceptor = validationInterceptor;
        final FhirContext context = new FhirContext(fhirConfig.versionEnum);
        context.setParserErrorHandler(new StrictErrorHandler());
        setFhirContext(context);        
        setResourceProviders(resourceProviders);
        setPlainProviders(systemProvider);
        setServerAddressStrategy(addressStrategy);

        if (context.getVersion().getVersion() == FhirVersionEnum.DSTU3) {
            final JpaConformanceProviderDstu3 confProvider =
                    new JpaConformanceProviderDstu3(this, (IFhirSystemDao<Bundle, Meta>) dao, daoConfig);
            confProvider.setImplementationDescription("ODA PHR Server");
            setServerConformanceProvider(confProvider);
        }
        else {
            final JpaConformanceProviderDstu2 confProvider = new JpaConformanceProviderDstu2(this,
                    (IFhirSystemDao<ca.uhn.fhir.model.dstu2.resource.Bundle, MetaDt>) dao, daoConfig);
            confProvider.setImplementationDescription("ODA PHR Server");
            setServerConformanceProvider(confProvider);
        }
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
        setETagSupport(ETagSupportEnum.ENABLED);

        final FhirContext ctx = getFhirContext();
        ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

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
        
        if (validationInterceptor.isPresent()){
            registerInterceptor(validationInterceptor.get());
        }
    }

}
