package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class AttributeGeometry extends PropertyName implements GeometryParameter {

    @Override
    public Geometry getTheGeom(DataSource ds, int fid) {
        try {
            return getFieldValue(ds, fid).getAsGeometry();
        } catch (DriverException ex) {
            return null;
        }
    }

}
