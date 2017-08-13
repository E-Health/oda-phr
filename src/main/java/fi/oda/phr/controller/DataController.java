package fi.oda.phr.controller;

import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.web.bind.annotation.*;

import ca.uhn.fhir.rest.client.IGenericClient;
import fi.oda.phr.config.*;

@RestController
public class DataController {
    private DatasetInitializer dataInitializer;

    private DataConfig dataConfig;

    private IGenericClient fhirClient;
    private final Logger log = LoggerFactory.getLogger(DataController.class);

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
