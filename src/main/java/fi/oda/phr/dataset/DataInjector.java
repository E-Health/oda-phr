package fi.oda.phr.dataset;

import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Implementing class injects test data to ODA PHR
 *
 */
public interface DataInjector {
    /**
     * Injects data to the server using the given client
     * @param client
     */
    void inject(IGenericClient client);
}
