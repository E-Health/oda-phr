package fi.oda.phr.controller;

import ca.uhn.fhir.rest.client.IGenericClient;
import fi.oda.phr.config.DataConfig;
import fi.oda.phr.config.DatasetInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class DataController {
    private final Logger log = LoggerFactory.getLogger(DataController.class);
    private DatasetInitializer dataInitializer;
    private DataConfig dataConfig;
    private IGenericClient fhirClient;

    public DataController(DatasetInitializer dataInitializer, IGenericClient fhirClient, DataConfig dataConfig) {
        this.dataConfig = dataConfig;
        this.fhirClient = fhirClient;
        this.dataInitializer = dataInitializer;
    }

    @RequestMapping(value = "/data/init", method = RequestMethod.POST)
    public void initData(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        log.debug("Feeding data...");
        dataInitializer.feedData(dataInitializer.parseDatasets(dataConfig), fhirClient);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        log.debug("Done");
    }

}
