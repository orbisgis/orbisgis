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
package org.orbisgis.legend;

import org.orbisgis.core.renderer.se.Symbolizer;

/**
 * {@code Legend} realizations represent patterns that can be found in a SE
 * style, and that match well-known configuration as described in the geographic
 * and cartographic literatures.
 *
 * @author alexis
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
	 * @return
	 */
	String getLegendTypeId();

	/**
	 * Returns a string that describes - for human - the nature of the
	 * thematic analysis made in this Legend.
	 * @return
	 */
	String getLegendTypeName();
}
