/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.core.ui.editorViews.toc.actions.cui.legend;

import org.orbisgis.legend.Legend;

/**
 * 
 * @author David Ortega
 */
public interface ILegendPanel  extends IRulePanel {

	/**
	 * It will return the Legend created by all the variables in the panel.
	 * 
	 * @return Legend
	 */
	public Legend getLegend();

	/**
	 * Sets the legend to be edited by this component
	 * 
	 * @param legend
	 */
	public void setLegend(Legend legend);

	/**
	 * Returns true if this legend can be applied to the specified geometry
	 * type.
	 * 
	 * @param geometryType
	 *            Type of geometry in the layer. One bit-or of the constants
	 *            POINT, LINE and POLYGON.
	 * @return
	 */
	public boolean acceptsGeometryType(int geometryType);

	/**
	 * @return {@code null} if the status of the edited legend is ok. An error message
	 *         if the legend cannot be created
	 */
	public String validateInput();

        /**
         * Gets the identifier of this panel.
         * @return
         */
        public String getId();

        /**
         * Associates an identifier to this panel. Particularly useful if we 
         * want to put this panel in a {@code CardLayout}.
         * @param newId 
         */
        public void setId(String newId);

        /**
         * Copy the {@code Legend} instance associated to this 
         * {@code ILegendPanel}. It is faster than copying the whole panel.
         * @return 
         */
        public Legend copyLegend();
}
