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
package org.orbisgis.legend.structure.literal;

import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.structure.parameter.NumericLegend;

/**
 * {@code Legend} associated to a numeric constant, that is represented as a
 * {@code RealLiteral} in the SE model.
 * @author Alexis Guéganno
 */
public class RealLiteralLegend implements NumericLegend {

        private  RealLiteral rl;

        /**
         * Build a new {@code RealLiteralLegend} that is associated to the
         * {@code RealLiteral r}.
         * @param r
         */
        public RealLiteralLegend(RealLiteral r){
                rl = r;
        }

        /**
         * Get the {@code RealLiteral} associated with this {@code
         * RealLiteralLegend}.
         * @return
         */
        public RealLiteral getLiteral() {
                return rl;
        }

        /**
         * As we're working on a RealLiteral, we can retrieve the double value
         * that is returned whatever the input data are.
         * @return
         */
        public double getDouble() {
            return rl.getValue(null, 0);
        }

        /**
         * As we're working on a RealLiteral, we can set the double value
         * that is returned whatever the input data are.
         * @param width
         */
        public void setDouble(double width) {
            rl.setValue(width);
        }

        @Override
        public SeParameter getParameter() {
                return getLiteral();
        }
}
