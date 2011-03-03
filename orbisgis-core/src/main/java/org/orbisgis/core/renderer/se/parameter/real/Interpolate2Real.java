package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.renderer.persistance.se.InterpolateType;
import org.orbisgis.core.renderer.persistance.se.InterpolationPointType;
import org.orbisgis.core.renderer.persistance.se.ModeType;

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

    @Override
    public double getValue(SpatialDataSourceDecorator sds, long fid) throws ParameterException {

		double value = this.lookupValue.getValue(sds, fid);

		if (i_points.get(0).getData() >= value){
			return i_points.get(0).getValue().getValue(sds, fid);
		}

		if (i_points.get(i_points.size()-1).getData() <= value){
			return i_points.get(i_points.size()-1).getValue().getValue(sds, fid);
		}

		int k = getFirstIP(value);

		InterpolationPoint<RealParameter> ip1 = i_points.get(k);
		InterpolationPoint<RealParameter> ip2 = i_points.get(k+1);

		switch(this.mode){
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
		this.fallbackValue.setContext(ctx);
		for (InterpolationPoint<RealParameter> ip : this.i_points){
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
