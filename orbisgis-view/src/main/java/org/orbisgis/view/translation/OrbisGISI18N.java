package org.orbisgis.view.translation;

/**
 * 
 * Simple class to load OrbisGIS translation file.
 * 
 */
public class OrbisGISI18N {
    /**
     * This is a static class
     */
    private OrbisGISI18N() {

    }
    static String getI18NPath() {
            return OrbisGISI18N.class.getCanonicalName();
    }
}
