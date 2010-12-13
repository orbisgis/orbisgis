package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.InterpolateType;
import org.orbisgis.core.renderer.persistance.se.InterpolationPointType;
import org.orbisgis.core.renderer.persistance.se.ModeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author maxence
 * @param <ToType> One of RealParameter or ColorParameter
 * @param <FallbackType> extends ToType (the LirealOne, please)...
 * @todo find a nice way to compute interpolation for RealParameter and ColorParameter
 *
 */
public abstract class Interpolate<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {

	protected InterpolationMode mode;
	protected RealParameter lookupValue;
	protected FallbackType fallbackValue;
	protected ArrayList<InterpolationPoint<ToType>> i_points;

	public enum InterpolationMode {
		LINEAR, COSINE, CUBIC
	}

    protected Interpolate(){
		this.i_points = new ArrayList<InterpolationPoint<ToType>>();
	}

	public Interpolate(FallbackType fallbackValue) {
		this.fallbackValue = fallbackValue;
		this.i_points = new ArrayList<InterpolationPoint<ToType>>();
	}

	@Override
	public final boolean dependsOnFeature() {
		if (this.getLookupValue().dependsOnFeature()) {
			return true;
		}

		int i;
		for (i = 0; i < this.getNumInterpolationPoint(); i++) {
			if (this.getInterpolationPoint(i).getValue().dependsOnFeature()) {
				return true;
			}
		}

		return false;
	}

	public void setFallbackValue(FallbackType fallbackValue) {
		this.fallbackValue = fallbackValue;
	}

	public FallbackType getFallbackValue() {
		return fallbackValue;
	}

	public void setLookupValue(RealParameter lookupValue) {
		this.lookupValue = lookupValue;
		if (this.lookupValue != null){
			this.lookupValue.setContext(RealParameterContext.realContext);
		}
	}

	public RealParameter getLookupValue() {
		return lookupValue;
	}

	/**
	 * Return the number of classes defined within the classification. According to this number (n),
	 *  available class value ID are [0;n] and ID for threshold are [0;n-1
	 *
	 *  @return number of defined class
	 */
	public int getNumInterpolationPoint() {
		return i_points.size();
	}

	/**
	 * Add a new interpolation point.
	 * The new point is inserted at the right place in the interpolation point list, according to its data
	 *
	 */
	public void addInterpolationPoint(InterpolationPoint<ToType> point) {
		i_points.add(point);
		sortInterpolationPoint();
	}

	public InterpolationPoint<ToType> getInterpolationPoint(int i) {
		return i_points.get(i);
	}

	public void setInterpolationMode(InterpolationMode mode) {
		this.mode = mode;
	}

	public InterpolationMode getInterpolationMode() {
		return mode;
	}

	private void sortInterpolationPoint() {
		Collections.sort(i_points);
	}

	@Override
	public ParameterValueType getJAXBParameterValueType() {
		ParameterValueType p = new ParameterValueType();
		p.getContent().add(this.getJAXBExpressionType());
		return p;
	}

	@Override
	public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
		InterpolateType i = new InterpolateType();

		if (fallbackValue != null) {
			i.setFallbackValue(fallbackValue.toString());
		}
		if (lookupValue != null) {
			i.setLookupValue(lookupValue.getJAXBParameterValueType());
		}

		if (mode != null) {
			i.setMode(ModeType.fromValue(mode.toString()));
		}

		List<InterpolationPointType> ips = i.getInterpolationPoint();


		for (InterpolationPoint<ToType> ip : i_points) {
			InterpolationPointType ipt = new InterpolationPointType();

			ipt.setValue(ip.getValue().getJAXBParameterValueType());
			ipt.setData(ip.getData());
			ips.add(ipt);
		}

		ObjectFactory of = new ObjectFactory();
		return of.createInterpolate(i);
	}

	protected int getFirstIP(double data) {
		int i = -1;
		for (InterpolationPoint ip : i_points) {
			if (ip.getData() > data) {
				return i;
			}
			i++;
		}
		return -1;
	}


	protected double cubicInterpolation(double d1, double d2, double x,
			double v1, double v2, double v3, double v4){
		double mu = (x - d1) / (d2 - d1);

		return 0.0;
	}

	protected double cosineInterpolation(double d1, double d2, double x, double v1, double v2) {
		double mu = (x - d1) / (d2 - d1);
		double mu2 = (1 - Math.cos(mu * Math.PI)) * 0.5;
		return v1 + mu2*(v2-v1);
	}

	protected double linearInterpolation(double d1, double d2, double x, double v1, double v2) {
		return v1 + (v2 - v1) * (x - d1) / (d2 - d1);
	}
}
