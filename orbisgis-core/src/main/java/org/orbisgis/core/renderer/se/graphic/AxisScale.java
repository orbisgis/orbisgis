package org.orbisgis.core.renderer.se.graphic;

import java.util.HashSet;
import net.opengis.se._2_0.thematic.AxisScaleType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class AxisScale {

    public static final double DEFAULT_LENGTH = 40;
    public static final double DEFAULT_MEASURE = 40;
    private RealParameter axisLength;
    private RealParameter measure;

    public AxisScale(){
        this.setAxisLength(new RealLiteral(DEFAULT_LENGTH));
        this.setMeasure(new RealLiteral(DEFAULT_MEASURE));
    }

    public AxisScale(AxisScaleType as) throws InvalidStyle {
		
       if (as.getAxisLength() != null){
           this.setAxisLength(SeParameterFactory.createRealParameter(as.getAxisLength()));
       }

       if (as.getValue() != null){
           this.setMeasure(SeParameterFactory.createRealParameter(as.getValue()));
       }
    }

    public RealParameter getMeasureValue() {
        return measure;
    }

    /**
     * Measure is the value that will be represented by a AxisLenght length
     * Cannot be null !
     * @param value not null
     */
    public void setMeasure(RealParameter value) {
		if (value != null){
            this.measure = value;
			measure.setContext(RealParameterContext.REAL_CONTEXT);
		}
    }

    public RealParameter getAxisLength() {
        return axisLength;
    }

    /**
     * The axis length that represent this.measure. Cannot be null
     * @param data
     */
    public void setAxisLength(RealParameter data) {
		if (data != null){
            this.axisLength = data;
			axisLength.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
		}
    }

    public AxisScaleType getJAXBType() {
        AxisScaleType scale = new AxisScaleType();

        if (axisLength != null) {
            scale.setAxisLength(axisLength.getJAXBParameterValueType());

        }
        if (measure != null) {
            scale.setValue(measure.getJAXBParameterValueType());
        }

        return scale;
    }

    /**
     * Gets the feature this {@code AxisScale} depends on.
     * @return
     */
    public HashSet<String> dependsOnFeature() {
        HashSet<String> ret = new HashSet<String>();
        if(axisLength != null){
            ret.addAll(axisLength.dependsOnFeature());
        }
        if(measure != null){
            ret.addAll(measure.dependsOnFeature());
        }
        return ret;
    }

    /**
     * Gets the analysis that are used to build this {@code AxisScale}.
     * @return
     */
    public UsedAnalysis getUsedAnalysis(){
        UsedAnalysis ua = new UsedAnalysis();
        ua.include(axisLength);
        ua.include(measure);
        return ua;
    }
}
