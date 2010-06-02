package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

import org.orbisgis.core.renderer.se.parameter.PropertyName;

public class ColorAttribute extends PropertyName implements ColorParameter {

    public ColorAttribute(String fieldName, DataSource ds) throws DriverException {
        super(fieldName, ds);
    }

    public ColorAttribute(JAXBElement<PropertyNameType> expr) {
        super(expr);
    }

    @Override
    public Color getColor(DataSource ds, long fid) throws ParameterException {
        try {
            return Color.getColor(getFieldValue(ds, (int) fid).getAsString()); //
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \"" + fieldName + "\"");
        }
    }
}
