
package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;


/**
 *
 * @author maxence
 * @todo implement SimpleFeature functions (buffer, etc)
 */
public abstract class GeometryFunction implements GeometryParameter {
    @Override
    public Geometry getTheGeom(Feature feat){
        return null;
    }
}
