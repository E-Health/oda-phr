package fi.oda.phr.server.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.dstu3.model.*;
import org.junit.*;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor.ActionRequestDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import fi.oda.common.fhir.utils.OdaFhirConstants;

public class InstanceIdTaggingInterceptorTest {
    private InstanceIdTaggingInterceptor interceptor;

    private ServletRequestDetails requestDetails;

    private HttpServletRequest theRequest;

    private String odaInstanceId = "testinstance";

    private Bundle bundle;

    private Person personResource;

    private RestfulServer mockServer;

    private ActionRequestDetails actionRequestDetails;

    @Before
    public void setUp() {
        personResource = new Person();
        interceptor = new InstanceIdTaggingInterceptor();
        requestDetails = mock(ServletRequestDetails.class);
        theRequest = mock(HttpServletRequest.class);
        mockServer = mock(RestfulServer.class);

        when(requestDetails.getServletRequest()).thenReturn(theRequest);
        when(theRequest.getHeader(OdaFhirConstants.ODA_INSTANCE_ID_HEADER)).thenReturn(odaInstanceId);
        when(mockServer.getFhirContext()).thenReturn(FhirContext.forDstu3());
        when(requestDetails.getServer()).thenReturn(mockServer);
        actionRequestDetails = new ActionRequestDetails(requestDetails);
        actionRequestDetails.setResource(personResource);
        bundle = new Bundle();
        bundle.addEntry().setResource(personResource);
    }

    @Test
    public void testAddTagToResource() {
        interceptor.incomingRequestPreHandled(RestOperationTypeEnum.CREATE, actionRequestDetails);

        Coding coding = personResource.getMeta().getTag().get(0);
        String tagSystem = coding.getSystem();
        String tagCode = coding.getCode();
        assertThat(tagSystem, is(OdaFhirConstants.ODA_INSTANCE_ID_SYSTEM));
        assertThat(tagCode, is(odaInstanceId));
    }

    @Test
    public void testAddTagToBundleResources() {
        actionRequestDetails.setResource(bundle);

        interceptor.incomingRequestPreHandled(RestOperationTypeEnum.TRANSACTION, actionRequestDetails);

        Coding coding = personResource.getMeta().getTag().get(0);
        String tagSystem = coding.getSystem();
        String tagCode = coding.getCode();
        assertThat(tagSystem, is(OdaFhirConstants.ODA_INSTANCE_ID_SYSTEM));
        assertThat(tagCode, is(odaInstanceId));
    }
}
