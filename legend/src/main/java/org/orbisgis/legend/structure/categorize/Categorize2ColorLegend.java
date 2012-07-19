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
package org.orbisgis.legend.structure.categorize;

import java.awt.Color;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.LegendStructure;

/**
 * {@code LegendStructure} that describes a color set using a {@code Categorize} instance.
 * @author alexis
 */
public class Categorize2ColorLegend implements LegendStructure {

        private Categorize2Color c2c;

        /**
         * Build the {@code LegendStructure} instance that describes a color computed
         * using a {@link Categorize2Color} instance.
         * @param categorize
         */
        public Categorize2ColorLegend(Categorize2Color categorize) {
                c2c = categorize;
        }

        /**
         * Get the {@link Categorize2Color} instance associated to this
         * {@code Categorize2ColorLegend}.
         * @return
         */
        public Categorize2Color getCategorize() {
                return c2c;
        }

        /**
         * Get the {@code Color} that is returned when an input value can't be
         * associated to any interval.
         * @return
         */
        public Color getFallBackColor() {
            ColorLiteral cl = c2c.getFallbackValue();
            return cl.getColor(null, -1);
        }

        /**
         * Get the {@code Color} that is returned when an input value can't be
         * associated to any interval.
         * @return
         */
        public void setFallBackColor(Color col) {
            ColorLiteral cl = c2c.getFallbackValue();
            cl.setColor(col);
        }

        /**
         * Get the {@code Color} that is returned for input values that are inferior
         * to the first threshold.
         * @param i
         * The index of the class we want to retrieve the {@code Color} from.
         * @return
         */
        public Color getColor(int i) {
            ColorLiteral cl = (ColorLiteral)c2c.getClassValue(i);
            return cl.getColor(null, -1);
        }

        /**
         * Set the {@code Color} that is returned for input values that are inferior
         * to the first threshold.
         * @param i
         * The index of the class we want to set the {@code Color}.
         * @param col
         */
        public void setColor(int i, Color col) {
            ColorLiteral cl = (ColorLiteral)c2c.getClassValue(i);
            cl.setColor(col);
        }

        /**
         * Get the number of classes defined in the inner {@code Categorize}.
         * @return
         */
        public int getNumClass() {
            return c2c.getNumClasses();
        }

        /**
         * Get the value of the ith threshold.
         * @param i
         * @return
         */
        public double getThreshold(int i) {
            RealLiteral rl = (RealLiteral) c2c.getClassThreshold(i);
            return rl .getValue(null, 0);
        }

        /**
         * Set the value of the ith threshold.
         * @param i
         * @param d
         */
        public void setThreshold(int i, double d) {
            RealLiteral rl = (RealLiteral) c2c.getClassThreshold(i);
            rl.setValue(d);
        }

        /**
         * Add a class to the inner {@code Categorize}.
         * @param threshold
         * @param col
         */
        public void addClass(double threshold, Color col) {
            ColorLiteral value = new ColorLiteral(col);
            RealLiteral thres = new RealLiteral(threshold);
            c2c.addClass(thres, value);
        }

        /**
         * Remove the ith class of this {@code Categorize}.
         * @param i
         */
        public void removeClass(int i) {
            c2c.removeClass(i);
        }

}
