package org.orbisgis.sif.translation;

/**
 * 
 * Simple class to load OrbisGIS translation file.
 * 
 */
public final class SIFI18N {
    /**
     * This is a static class
     */
    private SIFI18N() {

    }
    static String getI18NPath() {
            return SIFI18N.class.getCanonicalName();
    }
}
