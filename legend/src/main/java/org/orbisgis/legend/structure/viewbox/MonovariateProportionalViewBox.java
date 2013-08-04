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
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealFunction;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.legend.LookupFieldName;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;

/**
 * The representation of a monovariate proportional symbol, that has been
 * defined using a ViewBox. Such an analysis can be recognized if there is only
 * one dimension (ie height OR width) defined in the underlying {@code ViewBox},
 * and it is associated to an {@code Interpolation} that can be recognized as a
 * {@code SqrtInterpolationLegend}.
 * 
 * @author Alexis Guéganno
 */
public class MonovariateProportionalViewBox extends DefaultViewBox
        implements IInterpolationLegend {
        
        private boolean onH;

        /**
         * Builds a new {@code MonovariateProportionalViewBox}. It contains a
         * {@link ViewBox} with a null width and an interpolated height.
         * Interpolation is built such than 1 gives 1. Note that the lookup
         * value won't be set from this constructor, this has to be made
         * externally !
         */
        public MonovariateProportionalViewBox(){
                super();
                //We Work on height
                onH = true;
                ViewBox vb = getViewBox();
                //So we set the width to null
                vb.setWidth(null);
                setWidthLegend(null);
                //We build the height
                Interpolate2Real ir = new Interpolate2Real(new RealLiteral(0));
                InterpolationPoint<RealParameter> ip =new InterpolationPoint<RealParameter>();
                ip.setData(0);
                ip.setValue(new RealLiteral(0));
                ir.addInterpolationPoint(ip);
                InterpolationPoint<RealParameter> ip2 =new InterpolationPoint<RealParameter>();
                ip2.setData(1);
                ip2.setValue(new RealLiteral(1));
                ir.addInterpolationPoint(ip2);
                //We must not forget our interpolation function...
                //It's empty ! Don't forget to fill it later !
                RealFunction rf = new RealFunction(RealFunction.Operators.SQRT);
                try{
                        rf.addOperand(new RealAttribute());
                } catch(ParameterException pe){
                        throw new IllegalStateException("We've just failed at giving"
                                + "an operand to a log. Something must be going REALLY wrong...", pe);
                }
                ir.setLookupValue(rf);
                //We set the height
                vb.setHeight(ir);
            setHeightLegend(new SqrtInterpolationLegend(ir));
        }

        /**
         * Build a new {@code MonovariateProportionalViewBox} using the given
         * {@code SqrtInterpolationLegend} and {@code boolean} instances. {@code
         * onHeight} is used to know if the interpolation is made on the height
         * ({@code onHeight} is true) or on the width ({@code onHeight} is
         * false) of the associated {@link ViewBox};
         * @param inter
         * @param view 
         * @param onHeight
         */
        public MonovariateProportionalViewBox(SqrtInterpolationLegend inter, boolean onHeight,
                        ViewBox view){
                super(inter, onHeight, view);
                onH = onHeight;
        }

        /**
         * Get the {@link SqrtInterpolationLegend} associated to this {@code
         * Legend}.
         * @return
         * A {@link SqrtInterpolationLegend} instance.
         */
        public SqrtInterpolationLegend getInterpolation() {
                SqrtInterpolationLegend sqi;
                if(onH){
                        sqi = (SqrtInterpolationLegend) getHeightLegend();
                } else {
                        sqi = (SqrtInterpolationLegend) getWidthLegend();
                }
                return sqi;
        }

        /**
         * This method lets us check if the interpolation is made on the width
         * or on the height of the {@link ViewBox}.
         * @return
         * {@code true} if the interpolation is made on the height of the
         * {@code ViewBox}, ie if {@code getViewBox().getHeight()} returns a
         * not-null instance of {@code Interpolate}.
         */
        public boolean isOnHeight() {
                return onH;
        }

        @Override
        public double getFirstData() {
            return getInterpolation().getFirstData();
        }

        @Override
        public void setFirstData(double d) {
            getInterpolation().setFirstData(d);
        }

        @Override
        public double getSecondData() {
            return getInterpolation().getSecondData();
        }

        @Override
        public void setSecondData(double d) {
            getInterpolation().setSecondData(d);
        }

        @Override
        public double getFirstValue() throws ParameterException {
           return  getInterpolation().getFirstValue();
        }

        @Override
        public void setFirstValue(double d) {
            getInterpolation().setFirstValue(d);
        }

        @Override
        public double getSecondValue() throws ParameterException {
            return getInterpolation().getSecondValue();
        }

        @Override
        public void setSecondValue(double d) {
            getInterpolation().setSecondValue(d);
        }

        @Override
        public String getLookupFieldName(){
                return getInterpolation().getLookupFieldName();
        }

        @Override
        public void setLookupFieldName(String name){
                getInterpolation().setLookupFieldName(name);
        }

}
