package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.persistance.se.GeometryType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class GeometryAttribute extends PropertyName {

    public GeometryAttribute(JAXBElement<PropertyNameType> expr) throws InvalidStyle {
        super(expr);
	}

	public GeometryAttribute(PropertyNameType propertyName) throws InvalidStyle {
		super(propertyName);
	}


    public Geometry getTheGeom(SpatialDataSourceDecorator sds, long fid) throws ParameterException {
        try {
            return getFieldValue(sds, fid).getAsGeometry();
        } catch (DriverException ex) {
            throw new ParameterException("Could not fetch feature attribute \"" + fieldName + "\"");
        }
    }

	public GeometryType getJAXBGeometryType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
