package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.InterpolateType;
import net.opengis.se._2_0.core.InterpolationPointType;
import net.opengis.se._2_0.core.ModeType;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

public final class Interpolate2Real extends Interpolate<RealParameter, RealLiteral> implements RealParameter {


	private Double min;
	private Double max;
	private RealParameterContext ctx;

    public Interpolate2Real(RealLiteral fallback) {
        super(fallback);
		ctx = RealParameterContext.realContext;
    }

    public Interpolate2Real(JAXBElement<InterpolateType> expr) throws InvalidStyle {
		super();
		ctx = RealParameterContext.realContext;
        InterpolateType t = expr.getValue();

        this.setFallbackValue(new RealLiteral(t.getFallbackValue()));
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

    @Override
    public Double getValue(SpatialDataSourceDecorator sds, long fid) throws ParameterException {

		double value = this.getLookupValue().getValue(sds, fid);

		if (getInterpolationPoint(0).getData() >= value){
			return getInterpolationPoint(0).getValue().getValue(sds, fid);
		}

        int numPt = getNumInterpolationPoint();
		if (getInterpolationPoint(numPt-1).getData() <= value){
			return getInterpolationPoint(numPt -1).getValue().getValue(sds, fid);
		}

		int k = getFirstIP(value);

		InterpolationPoint<RealParameter> ip1 = getInterpolationPoint(k);
		InterpolationPoint<RealParameter> ip2 = getInterpolationPoint(k+1);

		switch(getMode()){
		case CUBIC:
			return cubicInterpolation(ip1.getData(), ip2.getData(), value,
			ip1.getValue().getValue(sds, fid), ip2.getValue().getValue(sds, fid), -1.0, -1.0);
		case COSINE:
			return cosineInterpolation(ip1.getData(), ip2.getData(), value,
			ip1.getValue().getValue(sds, fid), ip2.getValue().getValue(sds, fid));
		case LINEAR:
			return linearInterpolation(ip1.getData(), ip2.getData(), value,
			ip1.getValue().getValue(sds, fid), ip2.getValue().getValue(sds, fid));

		}
        return 0.0;
    }

	@Override
	public void setFallbackValue(RealLiteral l){
		super.setFallbackValue(l);
		if (l != null){
			l.setContext(ctx);
		}
	}

	@Override
	public void addInterpolationPoint(InterpolationPoint<RealParameter> point){
		RealParameter value = point.getValue();
		value.setContext(ctx);
		super.addInterpolationPoint(point);
	}

	@Override
	public String toString(){
		return "NA";
	}

	@Override
	public void setContext(RealParameterContext ctx) {
		this.ctx = ctx;
		this.getFallbackValue().setContext(ctx);
		for (InterpolationPoint<RealParameter> ip : getInterpolationPoints()){
			RealParameter value = ip.getValue();
			value.setContext(ctx);
		}
	}

	@Override
	public RealParameterContext getContext() {
		return ctx;
	}

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
