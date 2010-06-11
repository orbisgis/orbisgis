package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.InterpolateType;
import org.orbisgis.core.renderer.persistance.se.InterpolationPointType;
import org.orbisgis.core.renderer.persistance.se.ModeType;

import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;


public class Interpolate2Color extends Interpolate<ColorParameter, ColorLiteral> implements ColorParameter {

    public Interpolate2Color(ColorLiteral fallback){
        super(fallback);
    }
    
    public Interpolate2Color(JAXBElement<InterpolateType> expr) {
        InterpolateType t = expr.getValue();
        
        this.fallbackValue = new ColorLiteral(t.getFallbackValue());

        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));

        if (t.getMode() == ModeType.COSINE) {
            this.setInterpolationMode(InterpolationMode.COSINE);
        } else if (t.getMode() == ModeType.CUBIC) {
            this.setInterpolationMode(InterpolationMode.CUBIC);
        } else {
            this.setInterpolationMode(InterpolationMode.LINEAR);
        }

        for (InterpolationPointType ipt : t.getInterpolationPoint()){
            InterpolationPoint<ColorParameter> ip = new InterpolationPoint<ColorParameter>();

            ip.setData(ipt.getData());
            ip.setValue(SeParameterFactory.createColorParameter(ipt.getValue()));

            this.addInterpolationPoint(ip);
        }

    }
    /**
     *
     * @param ds
     * @param fid
     * @return
     */
    @Override
    public Color getColor(Feature feat){
        return Color.pink; // TODO compute interpolation
    }
}
