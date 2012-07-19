/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.legend.structure.interpolation;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.NumericLegend;

/**
 * The default representation of an interpolation, in the legend. If obtained
 * during an analysis, it is supposed to mean that non of the other, more
 * accurate cases, has been recognized in the input {@code Interpolate2Real}
 * instanceused by the analyzer.
 * @author Alexis Guéganno
 */
public class InterpolationLegend implements NumericLegend {

        private Interpolate2Real interp;

        /**
         * Build a new {@code InterpolationLegend}
         * @param inter
         */
        public InterpolationLegend(Interpolate2Real inter){
                interp = inter;
        }

        /**
         * Get the {@code Interpolate2Real} instance associated to this
         * {@code InterpolationLegend}.
         * @return
         */
        public Interpolate2Real getInterpolation(){
                return interp;
        }

        /**
         * Gets the data associated to the first interpolation point.
         * @return
         */
        public double getFirstData() {
            return interp.getInterpolationPoint(0).getData();
        }
        
        /**
         * Sets the data associated to the first interpolation point.
         * @return
         */
        public void setFirstData(double d) {
            interp.getInterpolationPoint(0).setData(d);
        }

        /**
         * Gets the data associated to the second interpolation point.
         * @param d
         */
        public double getSecondData() {
            return interp.getInterpolationPoint(1).getData();
        }

        /**
         * Sets the data associated to the second interpolation point.
         * @param d
         */
        public void setSecondData(double d) {
            interp.getInterpolationPoint(1).setData(d);
        }

        /**
         * Gets the value associated to the first interpolation point, as a double.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @return
         * @throws ParameterException
         * If the inner interpolation contain a value that is not a literal.
         */
        public double getFirstValue() throws ParameterException {
            return interp.getInterpolationPoint(0).getValue().getValue(null, 0);
        }

        /**
         * Sets the value associated to the first interpolation point.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @param d
         * @throws ClassCastException
         * If the inner interpolation contain a value that is not a literal.
         */
        public void setFirstValue(double d){
            RealLiteral rl = (RealLiteral) interp.getInterpolationPoint(0).getValue();
            rl.setValue(d);
        }

        /**
         * Gets the value associated to the second interpolation point, as a double.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @return
         * @throws ParameterException
         * If the inner interpolation contain a value that is not a literal.
         */
        public double getSecondValue() throws ParameterException {
            return interp.getInterpolationPoint(1).getValue().getValue(null, 0);
        }

        /**
         * Sets the value associated to the first interpolation point.
         * We are supposed to have check before initialization that the inner
         * interpolation is made with {@code RealLiteral} instances.
         * @param d
         * @throws ClassCastException
         * If the inner interpolation contain a value that is not a literal.
         */
        public void setSecondValue(double d){
            RealLiteral rl = (RealLiteral) interp.getInterpolationPoint(1).getValue();
            rl.setValue(d);
        }

}
