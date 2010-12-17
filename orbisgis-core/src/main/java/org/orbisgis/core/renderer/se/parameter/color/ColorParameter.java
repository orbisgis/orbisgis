package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import org.gdms.data.SpatialDataSourceDecorator;
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
    
    public abstract Color getColor(SpatialDataSourceDecorator sds, long fid) throws ParameterException;
}
