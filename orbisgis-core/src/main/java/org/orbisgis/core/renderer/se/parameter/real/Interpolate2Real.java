package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.InterpolateType;
import org.orbisgis.core.renderer.persistance.se.InterpolationPointType;
import org.orbisgis.core.renderer.persistance.se.ModeType;
import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

public class Interpolate2Real extends Interpolate<RealParameter, RealLiteral> implements RealParameter {

    public Interpolate2Real(RealLiteral fallback) {
        super(fallback);
    }

    public Interpolate2Real(JAXBElement<InterpolateType> expr) {
        InterpolateType t = expr.getValue();

        this.fallbackValue = new RealLiteral(t.getFallbackValue());
        this.setLookupValue(SeParameterFactory.createRealParameter(t.getLookupValue()));

        if (t.getMode() == ModeType.COSINE) {
            this.setInterpolationMode(InterpolationMode.COSINE);
        } else if (t.getMode() == ModeType.CUBIC) {
            this.setInterpolationMode(InterpolationMode.CUBIC);
        } else {
            this.setInterpolationMode(InterpolationMode.LINEAR);
        }

        for (InterpolationPointType ipt : t.getInterpolationPoint()){
            InterpolationPoint<RealParameter> ip = new InterpolationPoint<RealParameter>();

            ip.setData(ipt.getData());
            ip.setValue(SeParameterFactory.createRealParameter(ipt.getValue()));

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
    public double getValue(Feature feat) {
        return 0.0; // TODO compute interpolation
    }
}
