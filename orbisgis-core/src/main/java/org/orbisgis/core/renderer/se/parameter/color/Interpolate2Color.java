package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.InterpolateType;
import org.orbisgis.core.renderer.persistance.se.InterpolationPointType;
import org.orbisgis.core.renderer.persistance.se.ModeType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;


public class Interpolate2Color extends Interpolate<ColorParameter, ColorLiteral> implements ColorParameter {

    public Interpolate2Color(ColorLiteral fallback){
        super(fallback);
    }
    
    public Interpolate2Color(JAXBElement<InterpolateType> expr) throws InvalidStyle {
        InterpolateType t = expr.getValue();
        
        this.fallbackValue = new ColorLiteral(t.getFallbackValue());

        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));

		if (t.getMode() == ModeType.COSINE) {
            this.setInterpolationMode(InterpolationMode.COSINE);
        } else if (t.getMode() == ModeType.CUBIC) {
            this.setInterpolationMode(InterpolationMode.CUBIC);
        } else {
			System.out.println ("Fallback to linear mode !");
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
    public Color getColor(SpatialDataSourceDecorator sds, long fid) throws ParameterException{
		double value = this.lookupValue.getValue(sds, fid);

		if (i_points.get(0).getData() >= value){
			return i_points.get(0).getValue().getColor(sds, fid);
		}

		if (i_points.get(i_points.size()-1).getData() <= value){
			return i_points.get(i_points.size()-1).getValue().getColor(sds, fid);
		}


		int k = getFirstIP(value);

		InterpolationPoint<ColorParameter> ip1 = i_points.get(k);
		InterpolationPoint<ColorParameter> ip2 = i_points.get(k+1);

		double d1 = ip1.getData();
		double d2 = ip2.getData();

		Color c1 = ip1.getValue().getColor(sds, fid);
		Color c2 = ip2.getValue().getColor(sds, fid);


		switch(this.mode){
		case CUBIC:
			return new Color((int)cubicInterpolation(d1, d2, value, c1.getRed(), c2.getRed(), -1.0, -1.0),
					(int)cubicInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen(), -1.0, -1.0),
					(int)cubicInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue(), -1.0, 1.0));
		case COSINE:
			return new Color((int)cosineInterpolation(d1, d2, value, c1.getRed(), c2.getRed()),
					(int)cosineInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen()),
					(int)cosineInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue()));
		case LINEAR:
			return new Color((int)linearInterpolation(d1, d2, value, c1.getRed(), c2.getRed()),
					(int)linearInterpolation(d1, d2, value, c1.getGreen(), c2.getGreen()),
					(int)linearInterpolation(d1, d2, value, c1.getBlue(), c2.getBlue()));
		}
        return Color.pink; // TODO compute interpolation
    }
}
