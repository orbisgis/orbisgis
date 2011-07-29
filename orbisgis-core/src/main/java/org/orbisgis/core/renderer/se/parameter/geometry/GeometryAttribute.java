package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import net.opengis.se._2_0.core.GeometryType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

public class GeometryAttribute extends ValueReference {

    public GeometryAttribute(GeometryType geom) throws InvalidStyle {
        super(geom.getValueReference());
	}

    public Geometry getTheGeom(SpatialDataSourceDecorator sds, long fid) throws ParameterException {
        try {
            return getFieldValue(sds, fid).getAsGeometry();
        } catch (DriverException ex) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"");
        }
    }

	public GeometryType getJAXBGeometryType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
