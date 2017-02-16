package fi.oda.phr.dataset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;

/**
 *
 * Code for generating ODA patients from HL7 test data
 * TODO: Generate Finnish ids for the patients (HETU)
 */
public class PatientSetGenerator {
    public static final String DATA_PATH = "src/main/resources/";

    public static final String SOURCE_FILE = "patient-examples-cypress-template-dstu3.json";

    public static final String PATIENTS_FILE = "oda-patients.json";

    public static void main(String[] args) throws DataFormatException, IOException {
        final FhirContext ctx = FhirContext.forDstu3();
        final IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);

        final Bundle bundle = parser.parseResource(Bundle.class,
                new String(Files.readAllBytes(Paths.get(PatientSetGenerator.DATA_PATH, PatientSetGenerator.SOURCE_FILE))));
        bundle.setType(BundleType.TRANSACTION);
        final List<BundleEntryComponent> entries = bundle.getEntry();
        for (final BundleEntryComponent b : entries) {
            final Resource resource = b.getResource();
            if (resource instanceof Patient) {
                final Patient p = (Patient) resource;
                p.setId((IdDt) null);
                p.setManagingOrganization(null);
                b.setResource(p);
                final BundleEntryRequestComponent request = new BundleEntryRequestComponent();
                request.setMethod(HTTPVerb.POST);
                request.setUrl("Patient");
                b.setRequest(request);
            }
        }
        Files.write(Paths.get(PatientSetGenerator.DATA_PATH, PatientSetGenerator.PATIENTS_FILE),
                parser.encodeResourceToString(bundle).getBytes());
    }

}
