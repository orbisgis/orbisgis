package org.orbisgis.core.renderer.se.graphic;

import org.orbisgis.core.renderer.persistance.se.AxisScaleType;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class AxisScale {

    public RealParameter getValue() {
        return chartValue;
    }

    public void setValue(RealParameter value) {
        this.chartValue = value;
    }

    public RealParameter getData() {
        return axisLength;
    }

    public void setData(RealParameter data) {
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
