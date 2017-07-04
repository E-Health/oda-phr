package fi.oda.phr.controller;

import java.io.IOException;

import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.web.bind.annotation.*;

import fi.oda.phr.config.DatasetInitializer;

@RestController
public class DataController {
    private DatasetInitializer dataInitializer;

    private final Logger log = LoggerFactory.getLogger(DataController.class);
    public DataController(DatasetInitializer dataInitializer) {
        this.dataInitializer = dataInitializer;
    }

    @RequestMapping(value = "/data/init", method = RequestMethod.POST)
    public void initData(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        log.debug("Feeding data...");
        dataInitializer.feedData();
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        log.debug("Done");
    }
}
