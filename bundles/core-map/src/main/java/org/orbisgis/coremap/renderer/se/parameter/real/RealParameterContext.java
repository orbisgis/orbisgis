/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer.se.parameter.real;

import java.awt.Font;

/**
 * Instances of this define the boundaries where the value of a RealParameter can stand.
 * 
 * @author Maxence Laurent
 */
public class RealParameterContext {

	public static final RealParameterContext PERCENTAGE_CONTEXT;
        public static final RealParameterContext NON_NEGATIVE_CONTEXT;
        public static final RealParameterContext REAL_CONTEXT;
        
        protected Double min;
        protected Double max;
        
        static {
                PERCENTAGE_CONTEXT = new RealParameterContext(0.0, 1.0);
                NON_NEGATIVE_CONTEXT = new RealParameterContext(0.0, null);
                REAL_CONTEXT = new RealParameterContext(null, null);
        }

        /**
         * Instanciates a new RealParameterContext, with min as the lowest authorized
         * value and max as the highest.<br/>
         * Note that null values mean unbounded limit.
         * @param min
         * @param max 
         */
        public RealParameterContext(Double min, Double max) {
                this.min = min;
                this.max = max;
        }

        /**
         * Retrieve the lowest authorized value, or null if such a value is not set.
         * @return 
         */
        public Double getMin() {
                return min;
        }

        /**
         * Retrieve the highest authorized value, or null if such a value is not set.
         * @return 
         */
        public Double getMax() {
                return max;
        }

        @Override
        public String toString() {
                return " [" + min + ";" + max + "]";
        }

        /**
         * A {@code MarkIndexContext} is a {@code RealParameterContext} that is 
         * directly associated to a font. Built with the {@code Font}, it forces
         * the context to be between 0 and the size of the font minus one.
         */
        public class MarkIndexContext extends RealParameterContext {

                private Font font;

                /**
                 * Build a new {@code MarkIndexContext}, using {@code font} to 
                 * compute the boundaries of the context. That means the minimum
                 * authorized value is 0, and the maximum one is {@code 
                 * font.getNumGlyphs() - 1}.
                 * @param font 
                 */
                public MarkIndexContext(Font font) {
                        super(0.0, 0.0);
                        this.font = font;
                        this.max = (double) (font.getNumGlyphs() - 1);
                }

                /**
                 * Get the font associated to this context.
                 * @return 
                 */
                public Font getFont() {
                        return font;
                }
        }
}
