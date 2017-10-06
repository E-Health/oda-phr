package fi.oda.phr.server.interceptor;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import fi.oda.common.fhir.utils.OdaFhirConstants;

public class InstanceIdTaggingInterceptor extends InterceptorAdapter {

    @Override
    public void incomingRequestPreHandled(RestOperationTypeEnum theOperation, ActionRequestDetails theProcessedRequest) {
        ServletRequestDetails requestDetails = (ServletRequestDetails) theProcessedRequest.getRequestDetails();
        String instanceId = requestDetails.getServletRequest().getHeader(OdaFhirConstants.ODA_INSTANCE_ID_HEADER);
        if (StringUtils.isNotBlank(instanceId) && (theProcessedRequest.getResource() instanceof Resource)) {
            tagResource((Resource) theProcessedRequest.getResource(), instanceId);
        }
    }

    private void tagResource(Resource resource, String instanceId) {
        if (resource instanceof Bundle) {
            Bundle bundle = (Bundle) resource;
            if (bundle.getEntry() != null) {
                bundle.getEntry().stream().map(e -> e.getResource()).filter(Objects::nonNull).forEach(r -> tagResource(r, instanceId));
            }
        }
        else if (resource.getMeta() != null) {
            IBaseMetaType meta = resource.getMeta();
            if (meta.getTag().stream().noneMatch(t -> OdaFhirConstants.ODA_INSTANCE_ID_SYSTEM.equals(t.getSystem()))) {
                meta.addTag().setSystem(OdaFhirConstants.ODA_INSTANCE_ID_SYSTEM).setCode(instanceId);
            }
        }

    }
}
