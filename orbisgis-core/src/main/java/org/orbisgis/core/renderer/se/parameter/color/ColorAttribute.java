package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class ColorAttribute extends PropertyName implements ColorParameter {

    public ColorAttribute(String fieldName) {
        super(fieldName);
    }

    public ColorAttribute(JAXBElement<PropertyNameType> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public Color getColor(SpatialDataSourceDecorator sds, long fid) throws ParameterException {
        try {
            return Color.getColor(getFieldValue(sds, fid).getAsString());
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \"" + fieldName + "\"");
        }
    }
}
