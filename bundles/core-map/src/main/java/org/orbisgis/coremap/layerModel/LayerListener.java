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
package org.orbisgis.coremap.layerModel;

public interface LayerListener {

	/**
	 * The name of the layer has changed
	 * @deprecated Use the property change listener on the style property
	 * @param e
	 */
	void nameChanged(LayerListenerEvent e);

	/**
	 * the layer has became visible or invisible
	 * @deprecated Use the property change listener on the style property
	 * @param e
	 */
	void visibilityChanged(LayerListenerEvent e);

	/**
	 * The style of the layer has changed
	 * 
         * @deprecated Use the property change listener on the style property
	 * @param e
	 */
	void styleChanged(LayerListenerEvent e);

	/**
	 * A new layer has been added as a child
	 * 
	 * @param e
	 */
	void layerAdded(LayerCollectionEvent e);

	/**
	 * A child layer has been removed
	 * 
	 * @param e
	 */
	void layerRemoved(LayerCollectionEvent e);

	/**
	 * A child layer is going to be removed. Removal can be cancelled by
	 * returning false.
	 * 
	 * @param layerCollectionEvent
	 * @return
	 */
	boolean layerRemoving(LayerCollectionEvent layerCollectionEvent);

	/**
	 * A layer has been moved
	 * 
	 * @param e
	 */
	void layerMoved(LayerCollectionEvent e);

	/**
	 * The row selection of the layer has changed
	 * 
	 * @param e
	 */
	void selectionChanged(SelectionEvent e);

}
