package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class AttributeGeometry extends PropertyName implements GeometryParameter {

    @Override
    public Geometry getTheGeom(Feature feat) throws ParameterException {
        try {
            return getFieldValue(feat).getAsGeometry();
        } catch (DriverException ex) {
            throw new ParameterException("Could not fetch feature attribute \"" + fieldName + "\"");
        }
    }
}
