package fi.oda.phr.server.interceptor;


import java.util.*;
import java.util.stream.*;

import javax.servlet.http.*;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import fi.oda.common.fhir.utils.OdaFhirConstants;

/**
 * Server interceptor for appending odaInstanceId to national ids found in resources and query parameters.
 * 
 * National ids will be persisted in the following format: xxxxxx-xxxxINSTANCE-ID, where INSTANCE-ID
 * is the odaInstanceId obtained from ODA-INSTANCE-ID HTTP header.
 * 
 * The suffix is removed from identifiers in returned resources and bundles
 */
public class OdaInstanceIdConcatenatingInterceptor extends InterceptorAdapter {

    public static final String IDENTIFIER_PARAMETER = "identifier";

    public static final String DEBUG_KEEP_NATIONAL_ID_SUFFIX = "DEBUG-KEEP-NATIONAL-ID-SUFFIX";
    //Query parameters are processed for these types
    private Set<String> types = Collections.unmodifiableSet(
            Stream.of("Person")
                    .collect(Collectors.toSet()));

    /**
     * Concatenates odaInstanceId to nationalid query parameters
     */
    @Override
    public boolean incomingRequestPostProcessed(RequestDetails theRequestDetails, HttpServletRequest theRequest,
            HttpServletResponse theResponse) throws AuthenticationException {
        String resourceType = theRequestDetails.getResourceName();
        if (StringUtils.isNotBlank(resourceType) && types.contains(resourceType)) {
            ServletRequestDetails requestDetails = (ServletRequestDetails) theRequestDetails;
            String headerValue = requestDetails.getServletRequest().getHeader(OdaFhirConstants.ODA_INSTANCE_ID_HEADER);
            if (StringUtils.isNotBlank(headerValue)) {
                addToQueryParameters(requestDetails, headerValue);
            }
        }
        return true;
    }

    /**
     * Concatenates odaInstanceId to Person resource identifier (urn:oid:1.2.246.21)
     */
    @Override
    public void incomingRequestPreHandled(RestOperationTypeEnum theOperation, ActionRequestDetails theProcessedRequest) {
        ServletRequestDetails requestDetails = (ServletRequestDetails) theProcessedRequest.getRequestDetails();
        String headerValue = requestDetails.getServletRequest().getHeader(OdaFhirConstants.ODA_INSTANCE_ID_HEADER);
        IBaseResource resource = theProcessedRequest.getResource();
        if (StringUtils.isNotBlank(headerValue) && (resource instanceof Resource)) {
            addToResource((Resource) resource, headerValue);
        }

    }

    /**
     * Removes odaInstanceId from returned Person resources (also within Bundles)
     */
    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource resource) {
        ServletRequestDetails servletRequestDetails = (ServletRequestDetails) theRequestDetails;
        String removeSuffixHeader = servletRequestDetails.getServletRequest().getHeader(DEBUG_KEEP_NATIONAL_ID_SUFFIX);
        if (StringUtils.isNotBlank(removeSuffixHeader) && removeSuffixHeader.trim().equalsIgnoreCase("true")) {
            return true;
        }

        if (resource instanceof Resource) {
            removeFromResource((Resource) resource);
        }
        return true;
    }
    private void removeFromResource(Resource resource) {
        if (resource instanceof Bundle) {
            Bundle bundle = (Bundle) resource;
            if (bundle.getEntry() != null) {
                bundle.getEntry().stream().map(e -> e.getResource()).filter(Objects::nonNull).forEach(r -> removeFromResource(r));
            }
        }
        else if (resource instanceof Person) {
            List<Identifier> identifiers = ((Person) resource).getIdentifier();
            identifiers.stream().filter(i -> OdaFhirConstants.NATIONAL_ID_SYSTEM.equals(i.getSystem())).findFirst().ifPresent(
                    j -> j.setValue(j.getValue().substring(0, 11)));
        }
    }

    private void addToResource(Resource resource, String odaInstanceId) {
        if (resource instanceof Bundle) {
            Bundle bundle = (Bundle) resource;
            if (bundle.getEntry() != null) {
                bundle.getEntry().stream().map(e -> e.getResource()).filter(Objects::nonNull).forEach(r -> addToResource(r, odaInstanceId));
            }
        }
        else if (resource instanceof Person) {
            List<Identifier> identifiers = ((Person) resource).getIdentifier();

            identifiers.stream()
                    .filter(i -> OdaFhirConstants.NATIONAL_ID_SYSTEM.equals(i.getSystem()))
                    .filter(j -> j.getValue().length() == 11) //Add only to identifier without suffix.
                    .findFirst()
                    .ifPresent(k -> k.setValue(k.getValue() + odaInstanceId));
        }

    }

    private void addToQueryParameters(RequestDetails details, String odaInstanceId) {
        Map<String, String[]> parameters = details.getParameters();
        if (parameters.containsKey(IDENTIFIER_PARAMETER)) {
            parameters.put(IDENTIFIER_PARAMETER,
                    Arrays.stream(parameters.get(IDENTIFIER_PARAMETER))
                            .map(m -> processQueryIdentifier(m, odaInstanceId))
                            .toArray(String[]::new));
        }
    }

    private String processQueryIdentifier(String identifier, String odaInstanceId) {
        String result = identifier;
        String[] splitIdentifier = identifier.split("\\|");
        if (splitIdentifier.length == 2 && splitIdentifier[0].equals(OdaFhirConstants.NATIONAL_ID_SYSTEM)) {
            return result + odaInstanceId;
        }
        return result;
    }

}
