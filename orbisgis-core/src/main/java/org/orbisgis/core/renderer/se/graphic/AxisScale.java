/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.se.graphic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.opengis.se._2_0.thematic.AxisScaleType;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class AxisScale extends AbstractSymbolizerNode {

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
            measure.setParent(this);
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
            axisLength.setParent(this);
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
    @Override
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
    @Override
    public UsedAnalysis getUsedAnalysis(){
        UsedAnalysis ua = new UsedAnalysis();
        ua.merge(axisLength.getUsedAnalysis());
        ua.merge(measure.getUsedAnalysis());
        return ua;
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        if (axisLength != null) {
            ls.add(axisLength);
        }
        if (measure != null) {
            ls.add(measure);
        }
        return ls;
    }
    
}
