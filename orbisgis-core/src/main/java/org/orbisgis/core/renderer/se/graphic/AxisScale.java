package org.orbisgis.core.renderer.se.graphic;

import org.orbisgis.core.renderer.persistance.se.AxisScaleType;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class AxisScale {

    AxisScale(AxisScaleType as) {
       if (as.getAxisLength() != null){
           this.setAxisLength(SeParameterFactory.createRealParameter(as.getAxisLength()));
       }

       if (as.getChartValue() != null){
           this.setChartValue(SeParameterFactory.createRealParameter(as.getChartValue()));
       }
    }

    public RealParameter getChartValue() {
        return chartValue;
    }

    public void setChartValue(RealParameter value) {
        this.chartValue = value;
    }

    public RealParameter getAxisLength() {
        return axisLength;
    }

    public void setAxisLength(RealParameter data) {
        this.axisLength = data;
    }

    public AxisScaleType getJAXBType() {
        AxisScaleType scale = new AxisScaleType();

        if (axisLength != null) {
            scale.setAxisLength(axisLength.getJAXBParameterValueType());

        }
        if (chartValue != null) {
            scale.setChartValue(chartValue.getJAXBParameterValueType());
        }

        return scale;
    }
    private RealParameter axisLength;
    private RealParameter chartValue;
}
