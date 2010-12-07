package org.orbisgis.core.renderer.se.graphic;

import org.orbisgis.core.renderer.persistance.se.AxisScaleType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class AxisScale {

    AxisScale(AxisScaleType as) throws InvalidStyle {
		
       if (as.getAxisLength() != null){
           this.setAxisLength(SeParameterFactory.createRealParameter(as.getAxisLength()));
       }

       if (as.getMeasure() != null){
           this.setMeasure(SeParameterFactory.createRealParameter(as.getMeasure()));
       }
    }

    public RealParameter getMeasureValue() {
        return measure;
    }

    public void setMeasure(RealParameter value) {
        this.measure = value;
		if (measure != null){
			measure.setContext(RealParameterContext.realContext);
		}
    }

    public RealParameter getAxisLength() {
        return axisLength;
    }

    public void setAxisLength(RealParameter data) {
        this.axisLength = data;
		if (axisLength != null){
			axisLength.setContext(RealParameterContext.nonNegativeContext);
		}
    }

    public AxisScaleType getJAXBType() {
        AxisScaleType scale = new AxisScaleType();

        if (axisLength != null) {
            scale.setAxisLength(axisLength.getJAXBParameterValueType());

        }
        if (measure != null) {
            scale.setMeasure(measure.getJAXBParameterValueType());
        }

        return scale;
    }
    private RealParameter axisLength;
    private RealParameter measure;
}
