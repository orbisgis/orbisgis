package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;



/**
 * represent a color parameter
 * According to XSD, this color should not embed any alpha value !
 * Anyway, if alpha is defined within a ColorParameter, the value will be loosed
 * at serialization time !
 * @author maxence
 */
public interface ColorParameter extends SeParameter {
    
    /*
     * TODO Is (DataSource, featureId) the right way to access a feature ?
     */
    public abstract Color getColor(Feature feat) throws ParameterException;
}
