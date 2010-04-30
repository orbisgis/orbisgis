package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

public class Recode2Color extends Recode<ColorParameter, ColorLiteral> implements ColorParameter {

    public Recode2Color(ColorLiteral fallback, StringParameter lookupValue){
        super(fallback, lookupValue);
    }

    @Override
    public Color getColor(DataSource ds, int fid){
        try {
            // Should always depend on features !
            return getParameter(ds, fid).getColor(ds, fid);
        } catch (ParameterException ex) {
            return this.fallbackValue.getColor(ds, fid);
        }
    }

}
