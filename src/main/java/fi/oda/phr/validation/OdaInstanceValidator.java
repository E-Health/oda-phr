package fi.oda.phr.validation;

import java.util.List;

import org.hl7.fhir.dstu3.hapi.validation.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.*;

import ca.uhn.fhir.validation.IValidationContext;

public class OdaInstanceValidator extends FhirInstanceValidator {
    private List<Class<? extends IBaseResource>> ignoreList;
    private final Logger log = LoggerFactory.getLogger(OdaInstanceValidator.class);

    public OdaInstanceValidator(IValidationSupport theValidationSupport, List<Class<? extends IBaseResource>> ignoreList){
        super(theValidationSupport);
        this.ignoreList = ignoreList;
    }
    @Override
    public void validateResource(IValidationContext<IBaseResource> theCtx){        
        if (ignoreList.stream().anyMatch(e -> e.isInstance(theCtx.getResource()))){       
           log.debug("Resource " +  theCtx.getClass().getName() + " is in ignore list. Skipping validation.");
           return;
        }
        super.validateResource(theCtx);
    }
    
}
