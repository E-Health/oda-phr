package fi.oda.phr.validation;

import java.util.List;

import org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.dstu3.hapi.validation.IValidationSupport;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.validation.IValidationContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import fi.oda.phr.dataset.BundleInjector;

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
