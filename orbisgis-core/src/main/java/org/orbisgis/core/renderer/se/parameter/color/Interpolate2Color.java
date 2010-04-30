package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import org.gdms.data.DataSource;

import org.orbisgis.core.renderer.se.parameter.Interpolate;


public class Interpolate2Color extends Interpolate<ColorParameter, ColorLiteral> implements ColorParameter {

    public Interpolate2Color(ColorLiteral fallback){
        super(fallback);
    }

    /**
     *
     * @param ds
     * @param fid
     * @return
     */
    @Override
    public Color getColor(DataSource ds, int fid){
        return Color.pink; // TODO compute interpolation
    }
}
