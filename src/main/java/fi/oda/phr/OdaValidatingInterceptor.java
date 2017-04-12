package fi.oda.phr;

import java.util.Optional;

import org.hl7.fhir.dstu3.hapi.validation.DefaultProfileValidationSupport;
import org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.dstu3.hapi.validation.IValidationSupport;
import org.hl7.fhir.dstu3.hapi.validation.ValidationSupportChain;

import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import ca.uhn.fhir.validation.ResultSeverityEnum;
/**
 * Validates incoming fhir resources against ODA profiles and HL7 FHIR schemas.
 *  
 */
public class OdaValidatingInterceptor extends RequestValidatingInterceptor {
    /**
     *  
     * @param odaValidator
     *        If an empty Optional is given, validation is only performed against HL7 FHIR schemas.
     *        If ODA profiles should be used for validation, the Optional should contain a PrePopulatedValidationSupport
     */
    public OdaValidatingInterceptor(Optional<IValidationSupport> odaValidator){
        super();
        ValidationSupportChain validationChain;
        if (odaValidator.isPresent()){
            validationChain = new ValidationSupportChain(odaValidator.get(), new DefaultProfileValidationSupport());        
        }else{
            validationChain = new ValidationSupportChain(new DefaultProfileValidationSupport());
        }
        addValidatorModule(new FhirInstanceValidator(validationChain));
        setFailOnSeverity(ResultSeverityEnum.ERROR);
        setAddResponseHeaderOnSeverity(ResultSeverityEnum.WARNING);
        setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
        setResponseHeaderValueNoIssues("No issues detected");
    }
}
