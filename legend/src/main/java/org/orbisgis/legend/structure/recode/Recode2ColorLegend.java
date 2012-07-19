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

import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.legend.LegendStructure;

/**
 * LegendStructure associated to a {@code ColorParameter} set using a {@code
 * Recode2Color} instance.
 * @author Alexis Guéganno
 */
public class Recode2ColorLegend implements LegendStructure {

        private Recode2Color rc;

        /**
         * Build this {@code Recode2ColorLegend}, using the {@code Recode2Color}
         * instance given in argument.
         * @param rc
         */
        public Recode2ColorLegend(Recode2Color rc) {
                this.rc = rc;
        }

        /**
         * Get the {@code Recode2Color} instance associated to this {@code
         * Recode2ColorLegend}.
         * @return
         */
        public Recode2Color getRecode() {
                return rc;
        }

}
