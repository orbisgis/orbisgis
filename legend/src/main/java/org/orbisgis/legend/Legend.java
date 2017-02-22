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
package org.orbisgis.legend;

import org.orbisgis.coremap.renderer.se.Symbolizer;

/**
 * {@code Legend} realizations represent patterns that can be found in a SE
 * style, and that match well-known configuration as described in the geographic
 * and cartographic literatures.
 *
 * @author Alexis Guéganno
 */
public interface Legend extends LegendStructure {

        /**
         * Gets the associated {@code Symbolizer}.
         *
         * @return
         */
        Symbolizer getSymbolizer();

        /**
         * Get the name of the underlying {@code Symbolizer}.
         *
         * @return
         */
        String getName();

        /**
         * Set the name of the underlying {@code Symbolizer}.
         *
         * @param name
         */
        void setName(String name);

        /**
         * Get the minimum scale this {@code Legend} will be rendered for.
         *
         * @return
         */
        Double getMinScale();

        /**
         * Set the minimum scale this {@code Legend} will be rendered for.
         *
         * @param scale
         */
        void setMinScale(Double scale);

        /**
         * Get the maximum scale this {@code Legend} will be rendered for.
         *
         * @return
         */
        Double getMaxScale();

        /**
         * Set the maximum scale this {@code Legend} will be rendered for.
         *
         * @param scale
         */
        void setMaxScale(Double scale);

        /**
         * Returns an Id that will be used to check that a Legend can be edited
         * with a given edition panel.
         *
         * @return
         */
        String getLegendTypeId();

        /**
         * Returns a string that describes - for human - the nature of the
         * thematic analysis made in this Legend.
         *
         * @return
         */
        String getLegendTypeName();
}
