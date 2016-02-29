package org.orbisgis.wpsservice.model;

/**
 * GeometryData extends the ComplexData class.
 * It represents a geometry.
 *
 * @author Sylvain PALOMINOS
 */
public class GeometryData extends ComplexData {

    /**
     * Main Constructor.
     * @param format Format allowed.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    public GeometryData(Format format) throws MalformedScriptException {
        super(format);
    }
}
