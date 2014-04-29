/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.legend.structure.parameter.ParameterLegend;

/**
 * LegendStructure that can be associated to a simple string literal.
 * @author Alexis Guéganno
 */
public class StringLiteralLegend implements ParameterLegend{

        private StringLiteral literal;

        /**
         * Build a new {@code StringLiteralLegend} instance, using the literal
         * given in parameter.
         * @param literal
         */
        public StringLiteralLegend(StringLiteral literal) {
                this.literal = literal;
        }

        /**
         * Get the {@code Literal} associated to this {@code LegendStructure}
         * instance.
         * @return
         */
        public StringLiteral getLiteral() {
                return literal;
        }

        /**
         * Set the {@code Literal} associated to this {@code LegendStructure}
         * instance.
         * @param sl
         */
        public void setLiteral(StringLiteral sl) {
            literal = sl;
        }

        @Override
        public SeParameter getParameter() {
                return getLiteral();
        }

}
