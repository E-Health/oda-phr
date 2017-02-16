package fi.oda.phr.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

@Configuration
public class FhirDatabaseConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    /**
     * Configure FHIR properties around the the JPA server via this bean
     */
    @Bean
    public DaoConfig daoConfig() {
        final DaoConfig retVal = new DaoConfig();
        retVal.setSubscriptionEnabled(true);
        retVal.setSubscriptionPollDelay(5000);
        retVal.setSubscriptionPurgeInactiveAfterMillis(DateUtils.MILLIS_PER_HOUR);
        retVal.setAllowMultipleDelete(true);
        return retVal;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
        retVal.setPersistenceUnitName("HAPI_PU");
        retVal.setDataSource(dataSource);
        retVal.setPackagesToScan("ca.uhn.fhir.jpa.entity");
        retVal.setPersistenceProvider(new HibernatePersistenceProvider());
        retVal.setJpaProperties(jpaProperties());

        return retVal;
    }

    private Properties jpaProperties() {
        final Properties extraProperties = new Properties();
        extraProperties.put("hibernate.dialect", org.hibernate.dialect.DerbyTenSevenDialect.class.getName());
        extraProperties.put("hibernate.format_sql", "true");
        extraProperties.put("hibernate.show_sql", "false");
        extraProperties.put("hibernate.default_schema", "SA");
        extraProperties.put("hibernate.hbm2ddl.auto", "update");//cx"create-drop");//"update");
        //extraProperties.put("spring.jpa.hibernate.ddl-auto", "create-drop");//"update");
        extraProperties.put("hibernate.jdbc.batch_size", "20");
        extraProperties.put("hibernate.cache.use_query_cache", "false");
        extraProperties.put("hibernate.cache.use_second_level_cache", "false");
        extraProperties.put("hibernate.cache.use_structured_entries", "false");
        extraProperties.put("hibernate.cache.use_minimal_puts", "false");
        extraProperties.put("hibernate.search.default.directory_provider", "filesystem");
        extraProperties.put("hibernate.search.default.indexBase", "build/lucenefiles");
        extraProperties.put("hibernate.search.lucene_version", "LUCENE_CURRENT");
        //extraProperties.put("hibernate.search.default.worker.execution", "sync");
        return extraProperties;
    }

    /**
     * Do some fancy logging to create a nice access log that has details about each incoming request.
     */
    public IServerInterceptor loggingInterceptor() {
        final LoggingInterceptor retVal = new LoggingInterceptor();
        retVal.setLoggerName("fhirtest.access");
        retVal.setMessageFormat(
                "Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] Operation[${operationType} ${operationName} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] ResponseEncoding[${responseEncodingNoDefault}]");
        retVal.setLogExceptions(true);
        retVal.setErrorMessageFormat("ERROR - ${requestVerb} ${requestUrl}");
        return retVal;
    }

    /**
     * This interceptor adds some pretty syntax highlighting in responses when a browser is detected
     */
    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor responseHighlighterInterceptor() {
        final ResponseHighlighterInterceptor retVal = new ResponseHighlighterInterceptor();
        return retVal;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager retVal = new JpaTransactionManager();
        retVal.setEntityManagerFactory(entityManagerFactory);
        return retVal;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        /*   try {
            FileUtils.deleteDirectory(new File("C:/work/oda/oda-phr/build/libs/build/jpaserver_derby_files"));
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
    }
}
