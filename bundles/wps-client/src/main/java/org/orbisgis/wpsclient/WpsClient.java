package org.orbisgis.wpsclient;

import org.orbisgis.wpsservice.LocalWpsService;

/**
 * Interface that should be implemented by the OrbisGIS wps client.
 *
 * @author Sylvain PALOMINOS
 */
public interface WpsClient {

    /**
     * Refresh the JTree containing the list of the available wps processes.
     */
    void refreshAvailableScripts();

    /**
     * Returns the instance of the localWpsService of OrbisGIS.
     * @return The local instance of the wps service.
     */
    LocalWpsService getLocalWpsService();
}
