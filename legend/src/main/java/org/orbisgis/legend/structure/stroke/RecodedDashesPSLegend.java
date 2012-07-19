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
package org.orbisgis.legend.structure.stroke;

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;

/**
 * This class is associated to instances of {@code PenStroke} where a value
 * classification is made on the dash array.
 * @author Alexis Guéganno
 */
public class RecodedDashesPSLegend extends PenStrokeLegend{

        /**
         * Build a new instance of {@code LegendStructure} that matches a configuration
         * where a recode is made only on the dash array, and the other
         * elements are constant or null.
         * @param ps
         * @param width
         * @param fill
         * @param dash
         */
        public RecodedDashesPSLegend(PenStroke ps, LegendStructure width, LegendStructure fill, LegendStructure dash) {
                super(ps, width, fill, dash);
        }

}
