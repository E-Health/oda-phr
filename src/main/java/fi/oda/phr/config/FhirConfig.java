package fi.oda.phr.config;

import ca.uhn.fhir.context.FhirVersionEnum;

public class FhirConfig {

    public final FhirVersionEnum versionEnum;
    public final String path;

    public FhirConfig(FhirVersionEnum versionEnum, String path) {
        this.versionEnum = versionEnum;
        this.path = path;
    }
}
