/**
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
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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
 * info@orbisgis.org
 **/
package org.orbisgis.core.layerModel;

import com.vividsolutions.jts.geom.Envelope;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.gdms.data.DataSource;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.progress.ProgressMonitor;

/**
 * This interface provides information to the tool system and receives
 * notifications from it. Also registers the tool system as a listener in order
 * to notify it about certain events during edition
 */
public interface MapContext {
        //Properties index
        public static final String PROP_BOUNDINGBOX = "boundingBox";
        public static final String PROP_SELECTEDLAYERS = "selectedLayers";
        public static final String PROP_SELECTEDSTYLES = "selectedStyles";
        public static final String PROP_ACTIVELAYER = "activeLayer";
        public static final String PROP_LAYERMODEL = "layerModel";
        
        /**
        * Add a property-change listener for all properties.
        * The listener is called for all properties.
        * @param listener The PropertyChangeListener instance
        * @note Use EventHandler.create to build the PropertyChangeListener instance
        */
        void addPropertyChangeListener(PropertyChangeListener listener);
        /**
        * Add a property-change listener for a specific property.
        * The listener is called only when there is a change to 
        * the specified property.
        * @param prop The static property name PROP_..
        * @param listener The PropertyChangeListener instance
        * @note Use EventHandler.create to build the PropertyChangeListener instance
        */
        void addPropertyChangeListener(String prop,PropertyChangeListener listener);
        /**
        * Remove the specified listener from the list
        * @param listener The listener instance
        */
        void removePropertyChangeListener(PropertyChangeListener listener);

        /**
        * Remove the specified listener for a specified property from the list
        * @param prop The static property name PROP_..
        * @param listener The listener instance
        */
        public void removePropertyChangeListener(String prop,PropertyChangeListener listener);

        /**
         * Return a new layer corresponding to the provided data source
         * @param sds Spatial data source
         * @return A new layer linked with this data source
         * @throws LayerException The creation of the layer fail
         */
        public ILayer createLayer(DataSource sds) throws LayerException;

        /**
         * Return a new layer group
         * @param layerName Internal layer index
         * @return A layer group
         * @throws LayerException The creation of the layer fail
         */
	ILayer createLayerCollection(String layerName) throws LayerException;    
    
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
	 * Gets the selected rules
	 *
	 * @return
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	public Style[] getSelectedStyles() throws IllegalStateException;


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
	 * Set the mapcontext bounding-box (visible layers)
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
	 * Sets the selected styles. If the specified layers are not in the map
	 * context they are removed from selection.
	 *
	 * @param selectedLayers
	 * @throws IllegalStateException
	 *             If the map is closed
	 */
	public void setSelectedStyles(Style[] selectedStyles)
			throws IllegalStateException;

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
         * (re)-Initialise this map context with the provided data stream
         * @param in 
         * @throws IllegalArgumentException If the provided stream
         * doesn't comply with this context serialisation
         */
        public void read(InputStream in) throws IllegalArgumentException;
        
        /**
         * Serialisation of this map context into an output stream
         * @param out 
         */
        public void write(OutputStream out);

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
         * Draws an image of the layers in the specified MapTransform.
         * @param mt Contain the extent and the image to draw on
         * @param pm Object to report process and check the cancelled condition
         * @throws IllegalStateException
	 *             If the map is closed
         */
	void draw(MapTransform mt, ProgressMonitor pm)
			throws IllegalStateException;
        
        /**
         * Draws an image of the specified layer in the specified MapTransform.
         * @param mt Contain the extent and the image to draw on
         * @param pm Object to report process and check the cancelled condition
         * @param layer Draw recursively this layer
         * @throws IllegalStateException
	 *             If the map is closed
         */
	void draw(MapTransform mt, ProgressMonitor pm,ILayer layer)
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
