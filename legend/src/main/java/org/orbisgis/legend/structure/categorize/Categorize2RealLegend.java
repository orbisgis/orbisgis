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

import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.legend.LegendStructure;

/**
 * {@code LegendStructure} that describes a classification made by intervals on a real
 * parameter.
 * @author Alexis Guéganno
 */
public class Categorize2RealLegend implements LegendStructure {

        private Categorize2Real categorize;

        /**
         * Build a new {@code Categorize2Legend} instance, using the given
         * {@code Categorize2Real}.
         * @param c2r
         */
        public Categorize2RealLegend(Categorize2Real c2r){
                categorize = c2r;
        }

        /**
         * Get the {@code Categorize} instance associated to this {@code LegendStructure}
         * specialization.
         * @return
         */
        public Categorize2Real getCategorize() {
                return categorize;
        }
        
}
