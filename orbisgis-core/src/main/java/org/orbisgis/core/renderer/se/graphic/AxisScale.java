package org.orbisgis.core.renderer.se.graphic;

import org.orbisgis.core.renderer.persistance.se.AxisScaleType;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class AxisScale {

    AxisScale(AxisScaleType as) {
		
       if (as.getAxisLength() != null){
           this.setAxisLength(SeParameterFactory.createRealParameter(as.getAxisLength()));
       }

       if (as.getMeasureValue() != null){
           this.setMeasureValue(SeParameterFactory.createRealParameter(as.getMeasureValue()));
       }
    }

    public RealParameter getMeasureValue() {
        return measureValue;
    }

    public void setMeasureValue(RealParameter value) {
        this.measureValue = value;
		if (measureValue != null){
			measureValue.setContext(RealParameterContext.realContext);
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
        if (measureValue != null) {
            scale.setMeasureValue(measureValue.getJAXBParameterValueType());
        }

        return scale;
    }
    private RealParameter axisLength;
    private RealParameter measureValue;
}
