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
package org.orbisgis.legend.structure.recode;

import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.legend.structure.parameter.ParameterLegend;

/**
 * Specialization of {@code LegendStructure} that is used to represent value
 * classifications that are used as {@code StringParameter} instances.
 * @author Alexis Guéganno
 */
public class Recode2StringLegend implements ParameterLegend {

        private Recode2String recode;

        /**
         * Build a new instance of {@code Recode2StringLegend}, using the {@code
         * Recode} given in argument.
         * @param recode
         */
        public Recode2StringLegend(Recode2String recode) {
                this.recode = recode;
        }

        /**
         * Get the {@code Recode} instance associated to this {@code LegendStructure}.
         * @return
         */
        public Recode2String getRecode() {
                return recode;
        }

        @Override
        public SeParameter getParameter() {
                return getRecode();
        }

}
