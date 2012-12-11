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
package org.orbisgis.legend;

/**
 * Anlyzers are used to search for patterns in a symbology structure. Applied to
 * a given node, the analyzer must :
 * <ul><li>Detect patterns that can be seen directly in the node.</li>
 * <li>Analyze inner node, if useful, to detect patterns that are present deeper
 * in the style's tree</li>
 * <li>Be able to return all the found legends.</li>
 * <li>Build, from all the informations thus gathered, a LegendNode that
 * directly describes the cartographic nature of the analyzed node. It won't
 * always be possible, though.</li></ul>
 * @author Alexis Guéganno
 */
public interface Analyzer {

        /**
         * Get all the elements that are, or are part of, a thematic analysis
         * in this legend. These elements are returned as {@code LegendStructure}
         * instances, stored in a {@code List}.
         * @return
         * A {@code List} of {@code LegendStructure}s.
         */
        LegendStructure getLegend();

}
