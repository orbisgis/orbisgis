/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer.se.parameter.real;

import java.awt.Font;

/**
 * Instances of this define the boundaries where the value of a RealParameter can stand.
 * 
 * @author maxence
 */
public class RealParameterContext {

	public final static RealParameterContext PERCENTAGE_CONTEXT;
        public final static RealParameterContext NON_NEGATIVE_CONTEXT;
        public final static RealParameterContext REAL_CONTEXT;
        
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

        public class MarkIndexContext extends RealParameterContext {

                private Font font;

                public MarkIndexContext(Font font) {
                        super(0.0, 0.0);
                        this.font = font;
                        this.max = (double) (font.getNumGlyphs() - 1);
                }

                public Font getFont() {
                        return font;
                }
        }
}
