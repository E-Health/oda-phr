package fi.oda.phr.server.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.servlet.http.*;

import org.hl7.fhir.dstu3.model.*;
import org.junit.*;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor.ActionRequestDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import fi.oda.common.fhir.utils.OdaFhirConstants;

public class OdaInstanceIdConcatenatingInterceptorTest {
    private OdaInstanceIdConcatenatingInterceptor interceptor;

    private ServletRequestDetails requestDetails;

    private HttpServletRequest theRequest;

    private HttpServletResponse theResponse;

    private String resourceName = "Person";

    private String odaInstanceId = "testinstance";

    private String nationalId = "010101-0101";

    private String nationalIdQueryParam = OdaFhirConstants.NATIONAL_ID_SYSTEM + "|" + nationalId;

    private String concatenatedNationalId = nationalId + odaInstanceId;

    private String concatenatedQueryParam = OdaFhirConstants.NATIONAL_ID_SYSTEM + "|" + concatenatedNationalId;

    private Bundle bundle;
    private Person personResource;
    private Map<String, String[]> queryParams;

    private RestfulServer mockServer;
    private ActionRequestDetails actionRequestDetails;

    @Before
    public void setUp() {
        queryParams = new HashMap<String, String[]>();
        queryParams.put(OdaInstanceIdConcatenatingInterceptor.IDENTIFIER_PARAMETER, new String[]
            { nationalIdQueryParam });
        personResource = new Person();
        personResource.addIdentifier().setSystem(OdaFhirConstants.NATIONAL_ID_SYSTEM).setValue(nationalId);
        interceptor = new OdaInstanceIdConcatenatingInterceptor();
        requestDetails = mock(ServletRequestDetails.class);
        theRequest = mock(HttpServletRequest.class);
        theResponse = mock(HttpServletResponse.class);
        mockServer = mock(RestfulServer.class);
        when(requestDetails.getHeader(OdaFhirConstants.ODA_INSTANCE_ID_HEADER)).thenReturn(odaInstanceId);
        when(requestDetails.getResourceName()).thenReturn(resourceName);

        when(requestDetails.getParameters()).thenReturn(queryParams);
        when(theRequest.getHeader(OdaFhirConstants.ODA_INSTANCE_ID_HEADER)).thenReturn(odaInstanceId);
        when(mockServer.getFhirContext()).thenReturn(FhirContext.forDstu3());
        when(requestDetails.getServer()).thenReturn(mockServer);
        
        actionRequestDetails = new ActionRequestDetails(requestDetails);
        actionRequestDetails.setResource(personResource);
        bundle = new Bundle();
        bundle.addEntry().setResource(personResource);
    }


    @Test
    public void concatenateToQueryParam() {
        interceptor.incomingRequestPostProcessed(requestDetails, theRequest, theResponse);

        String producedQueryParam = queryParams.get(OdaInstanceIdConcatenatingInterceptor.IDENTIFIER_PARAMETER)[0];
        assertThat(producedQueryParam, is(concatenatedQueryParam));
    }

    @Test
    public void concatenateToPersonIdentifier() {
        interceptor.incomingRequestPreHandled(RestOperationTypeEnum.CREATE, actionRequestDetails);

        Identifier producedPersonIdentifier = personResource.getIdentifier().get(0);
        assertThat(producedPersonIdentifier.getSystem(), is(OdaFhirConstants.NATIONAL_ID_SYSTEM));
        assertThat(producedPersonIdentifier.getValue(), is(concatenatedNationalId));
    }

    @Test
    public void concatenateToBundlePersonIdentifier() {
        actionRequestDetails.setResource(bundle);

        interceptor.incomingRequestPreHandled(RestOperationTypeEnum.CREATE, actionRequestDetails);

        Identifier producedPersonIdentifier = personResource.getIdentifier().get(0);
        assertThat(producedPersonIdentifier.getSystem(), is(OdaFhirConstants.NATIONAL_ID_SYSTEM));
        assertThat(producedPersonIdentifier.getValue(), is(concatenatedNationalId));
    }

    @Test
    public void removeBundlePersonIdentifierSuffix() {
        personResource.getIdentifier().get(0).setValue(concatenatedNationalId);

        interceptor.outgoingResponse(requestDetails, bundle);

        Identifier producedPersonIdentifier = personResource.getIdentifier().get(0);
        assertThat(producedPersonIdentifier.getSystem(), is(OdaFhirConstants.NATIONAL_ID_SYSTEM));
        assertThat(producedPersonIdentifier.getValue(), is(nationalId));
    }

    @Test
    public void removePersonIdentifierSuffix() {
        personResource.getIdentifier().get(0).setValue(concatenatedNationalId);

        interceptor.outgoingResponse(requestDetails, personResource);

        Identifier producedPersonIdentifier = personResource.getIdentifier().get(0);
        assertThat(producedPersonIdentifier.getSystem(), is(OdaFhirConstants.NATIONAL_ID_SYSTEM));
        assertThat(producedPersonIdentifier.getValue(), is(nationalId));
    }

    @Test
    public void doNotRemovePersonIdentifierSuffixInDebug() {
        when(requestDetails.getHeader(OdaInstanceIdConcatenatingInterceptor.DEBUG_KEEP_NATIONAL_ID_SUFFIX)).thenReturn("true");
        personResource.getIdentifier().get(0).setValue(concatenatedNationalId);

        interceptor.outgoingResponse(requestDetails, personResource);

        Identifier producedPersonIdentifier = personResource.getIdentifier().get(0);
        assertThat(producedPersonIdentifier.getSystem(), is(OdaFhirConstants.NATIONAL_ID_SYSTEM));
        assertThat(producedPersonIdentifier.getValue(), is(concatenatedNationalId));
    }

}
