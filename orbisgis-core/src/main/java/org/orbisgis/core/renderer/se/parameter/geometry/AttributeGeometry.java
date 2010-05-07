package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class AttributeGeometry extends PropertyName implements GeometryParameter {

    @Override
    public Geometry getTheGeom(SpatialDataSourceDecorator sds, long fid) {
        try {
            return getFieldValue(sds, (int)fid).getAsGeometry();
        } catch (DriverException ex) {
            return null;
        }
    }

}
