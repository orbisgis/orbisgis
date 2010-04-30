package org.orbisgis.core.renderer.se.parameter.color;


import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;

import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;


public class Categorize2Color extends Categorize<ColorParameter, ColorLiteral> implements ColorParameter {

    public Categorize2Color(ColorParameter initialClass, ColorLiteral fallback, RealParameter lookupValue){
        super(initialClass, fallback, lookupValue);
    }

    @Override
    public Color getColor(DataSource ds, int fid){
        try {
            return getParameter(ds, fid).getColor(ds, fid);
        } catch (ParameterException ex) {
            // fetch the fallback  value
            // it's a literal so no need to access the feature
            return this.fallbackValue.getColor(null, 0);
        }
    }
}
