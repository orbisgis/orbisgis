package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.persistance.se.GeometryType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class GeometryAttribute extends PropertyName implements GeometryParameter {

	public GeometryAttribute(LiteralType literalType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

    @Override
    public Geometry getTheGeom(Feature feat) throws ParameterException {
        try {
            return getFieldValue(feat).getAsGeometry();
        } catch (DriverException ex) {
            throw new ParameterException("Could not fetch feature attribute \"" + fieldName + "\"");
        }
    }

	@Override
	public GeometryType getJAXBGeometryType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
