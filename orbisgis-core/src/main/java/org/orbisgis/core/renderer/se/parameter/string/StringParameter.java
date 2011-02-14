package org.orbisgis.core.renderer.se.parameter.string;

import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;


/**
 *
 * @author maxence
 * @todo implement 05-077r4 11.6.1, 11.6.2, 11.6.3 (String, number and date formating)
 */
public interface StringParameter extends SeParameter {
    
    /*
     * TODO Is (DataSource, featureId) the right way to access a feature ?
     */
    public abstract String getValue(SpatialDataSourceDecorator sds, long fid) throws ParameterException;
}
