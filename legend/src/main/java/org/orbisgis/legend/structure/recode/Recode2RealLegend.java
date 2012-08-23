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
package org.orbisgis.legend.structure.recode;

import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.legend.structure.parameter.ParameterLegend;

/**
 * {@code LegendStructure} specialization associated to {@code Recode2Real} instances.
 * @author Alexis Guéganno
 */
public class Recode2RealLegend implements ParameterLegend {

        private Recode2Real recode;

        /**
         * Build a new {@code Recode2Real} instance, using the given {@code
         * Recode2Real}.
         * @param recode
         */
        public Recode2RealLegend(Recode2Real recode) {
                this.recode = recode;
        }

        /**
         * Get the {@code Recode2Real} associated to this {@code LegendStructure}
         * @return
         */
        public Recode2Real getRecode() {
                return recode;
        }

        @Override
        public SeParameter getParameter() {
                return getRecode();
        }

}
