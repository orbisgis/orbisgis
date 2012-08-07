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
package org.orbisgis.legend.structure.stroke;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;

/**
 * {@code PenStroke} is defined using a {@code Width} attribute, which is a
 * RealAttribute. Consequently, it can be defined as linearly interpolated upon
 * some numeric attribute. This way, we obtain a "proportional line" analysis.
 * @author Alexis Guéganno
 */
public class ProportionalStrokeLegend extends PenStrokeLegend {


        /**
         * Build a new {@code ProportionalStrokeLegend}, using the given {@code
         * PenStroke}.
         * @param penStroke
         */
        public ProportionalStrokeLegend(PenStroke penStroke, LinearInterpolationLegend width,
                    LegendStructure fill, LegendStructure dashes) {
                super(penStroke, width, fill, dashes);
        }

        /**
         * Get the data of the second interpolation point
         * @return
         */
        public double getFirstData() {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getFirstData();
        }

        /**
         * Get the data of the first interpolation point
         * @return
         */
        public double getSecondData() {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getSecondData();
        }

        /**
         * Set the data of the second interpolation point
         * @param d
         */
        public void setFirstData(double d) {
            ((LinearInterpolationLegend)getWidthAnalysis()).setFirstData(d);
        }

        /**
         * Set the data of the first interpolation point
         * @param d
         */
        public void setSecondData(double d){
            ((LinearInterpolationLegend)getWidthAnalysis()).setSecondData(d);
        }

        /**
         * Get the value of the first interpolation point, as a {@code double}. The
         * interpolation value is supposed to be a {@code RealLiteral} instance. If
         * it is not, an exception should have been thrown at initialization.
         * @return
         */
        public double getFirstValue() throws ParameterException {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getFirstValue();
        }

        /**
         * Set the value of the first interpolation point, as a {@code double}.
         * @param d
         */
        public void setFirstValue(double d) {
            ((LinearInterpolationLegend)getWidthAnalysis()).setFirstValue(d);
        }
        
        /**
         * Get the value of the second interpolation point, as a {@code double}. The
         * interpolation value is supposed to be a {@code RealLiteral} instance. If
         * it is not, an exception should have been thrown at initialization.
         * @return
         */
        public double getSecondValue() throws ParameterException {
            return ((LinearInterpolationLegend)getWidthAnalysis()).getSecondValue();
        }

        /**
         * Set the value of the second interpolation point, as a {@code double}.
         * @param d 
         */
        public void setSecondValue(double d) {
            ((LinearInterpolationLegend)getWidthAnalysis()).setSecondValue(d);
        }
        
}
