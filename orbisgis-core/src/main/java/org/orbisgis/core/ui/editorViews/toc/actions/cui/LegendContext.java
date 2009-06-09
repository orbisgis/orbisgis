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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;

public interface LegendContext {

	/**
	 * Gets the type of geometries in the specified layer. One of the constants
	 * in the {@link ILegendPanel}
	 *
	 * @return
	 */
	int getGeometryType();

	/**
	 * return true if the layer can contain points
	 *
	 * @return
	 */
	boolean isPoint();

	/**
	 * return true if the layer can contain line
	 *
	 * @return
	 */
	boolean isLine();

	/**
	 * return true if the layer can contain polygon
	 *
	 * @return
	 */
	boolean isPolygon();

	/**
	 * Gets the constraint of the field which legend is being edited
	 *
	 * @return
	 */
	GeometryConstraint getGeometryConstraint();

	/**
	 * Gets the layer which legend is being edited
	 *
	 * @return
	 */
	ILayer getLayer();

	/**
	 * Gets the transform of the current map editor
	 *
	 * @return
	 */
	MapTransform getCurrentMapTransform();

	/**
	 * Gets all the available symbol editors
	 *
	 * @return
	 */
	ISymbolEditor[] getAvailableSymbolEditors();
}
