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
package org.orbisgis.core.layerModel;

import java.awt.image.BufferedImage;
import org.gdms.data.DataSource;

import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

/**
 * This interface provides information to the tool system and receives
 * notifications from it. Also registers the tool system as a listener in order
 * to notify it about certain events during edition
 */
public interface MapContext {

	/**
	 * Gets the root layer of the layer collection in this edition context
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	ILayer getLayerModel() throws IllegalStateException;

	/**
	 * Gets all the layers in the map context
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	public ILayer[] getLayers() throws IllegalStateException;

	/**
	 * Gets the selected layers
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	public ILayer[] getSelectedLayers() throws IllegalStateException;

	/**
	 * Adds a listener for map context events
	 * 
	 * @param listener
	 */
	public void addMapContextListener(MapContextListener listener);

	/**
	 * This method is uses instead of get id to have a unique id for a
	 * mapcontext that cannot change.Based on time creation
	 * 
	 * @return a unique identifier for the mapContext
	 */
	long getIdTime();

	/**
	 * Get the mapcontext boundingbox (visible layers)
	 * 
	 * @return
	 */
	public Envelope getBoundingBox();

	/**
	 * Set the mapcontext boundingbox (visible layers)
	 * 
	 * @param extent
	 */
	void setBoundingBox(Envelope extent);

	/**
	 * Removes a listener for map context events
	 * 
	 * @param listener
	 */
	public void removeMapContextListener(MapContextListener listener);

	/**
	 * Sets the selected layers. If the specified layers are not in the map
	 * context they are removed from selection.
	 * 
	 * @param selectedLayers
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	public void setSelectedLayers(ILayer[] selectedLayers)
			throws IllegalStateException;

	/**
	 * Returns a JAXB object containing all the persistent information of this
	 * MapContext
	 * 
	 * @return
	 */
	Object getJAXBObject();

	/**
	 * Populates the content of this MapContext with the information stored in
	 * the specified JAXB Object. The map must be closed.
	 * 
	 * @param jaxbObject
	 * @throws IllegalStateException
	 *             If the map is open
	 */
	void setJAXBObject(Object jaxbObject) throws IllegalStateException;

	/**
	 * Opens all the layers in the map. All layers added to an open map are
	 * opened automatically. Layers that cannot be created are removed from the
	 * layer tree and an error message is sent to the ErrorManager service
	 * 
	 * @param pm
	 * @throws LayerException
	 *             If some layer cannot be open. In this case all already open
	 *             layers are closed again
	 * @throws IllegalStateException
	 *             If the map is already open
	 */
	void open(ProgressMonitor pm) throws LayerException, IllegalStateException;

	/**
	 * Closes all the layers in the map
	 * 
	 * @param pm
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	void close(ProgressMonitor pm) throws IllegalStateException;

	/**
	 * Return true if this map context is open and false otherwise
	 * 
	 * @return
	 */
	boolean isOpen();

	/**
	 * Draws an image of the layers in the specified image.
	 * 
	 * @param inProcessImage
	 *            Image where the drawing will take place
	 * @param extent
	 *            Extent of the data to take into account. It must have the same
	 *            proportions than the image
	 * @param pm
	 *            Object to report process and check the cancelation condition
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	void draw(BufferedImage inProcessImage, Envelope extent, ProgressMonitor pm)
			throws IllegalStateException;

	/**
	 * Gets the layer where all the edition actions take place
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	ILayer getActiveLayer() throws IllegalStateException;

	/**
	 * Sets the layer where all the edition actions take place
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	void setActiveLayer(ILayer activeLayer) throws IllegalStateException;

        /**
         * @return the selectionInducedRefresh
         */
        boolean isSelectionInducedRefresh();

        /**
         * @param selectionInducedRefresh the selectionInducedRefresh to set
         */
        void setSelectionInducedRefresh(boolean selectionInducedRefresh);

        void checkSelectionRefresh(final int[] selectedRows, final int[] oldSelectedRows, final DataSource dataSource);

	/**
	 * get the mapcontext {@link CoordinateReferenceSystem}
	 * 
	 * @return
	 */

	// CoordinateReferenceSystem getCoordinateReferenceSystem();

	/**
	 * set the {@link CoordinateReferenceSystem} to the mapcontext
	 * 
	 * @param crs
	 */
	// void setCoordinateReferenceSystem(CoordinateReferenceSystem crs);

}
