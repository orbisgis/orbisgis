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
package org.orbisgis.view.toc.actions.cui.legend;

import org.orbisgis.legend.Legend;
import org.orbisgis.view.toc.actions.cui.LegendContext;

/**
 * Interface for Legend UI panels in the SimpleStyleEditor.
 *
 * @author David Ortega
 * @author Alexis Guéganno
 */
public interface ILegendPanel extends ISELegendPanel {

    /**
     * Initialize this panel using the given context and legend. Both must
     * be not null and {@code leg} must be acceptable by this panel
     * @param lc The input context
     * @param leg The input legend
     * @throws IllegalArgumentException If the provided legend is not acceptable.
     */
    void initialize(LegendContext lc, Legend leg);

	/**
	 * It will return the Legend created by all the variables in the panel.
	 * 
	 * @return Legend
	 */
	Legend getLegend();

	/**
	 * Sets the legend to be edited by this component
	 * 
	 * @param legend
	 */
	void setLegend(Legend legend);

    /**
     * Sets the type of the geometry field of the data that must be
     * represented.
     * @param type
     */
    void setGeometryType(int type);

	/**
	 * Returns true if this legend can be applied to the specified geometry
	 * type.
	 * 
	 * @param geometryType
	 *            Type of geometry in the layer. One bit-or of the constants
	 *            POINT, LINE and POLYGON.
	 * @return
	 */
	boolean acceptsGeometryType(int geometryType);

    /**
     * Copy the {@code Legend} instance associated to this
     * {@code ILegendPanel}. It is faster than copying the whole panel.
     * @return
     */
    Legend copyLegend();
}
