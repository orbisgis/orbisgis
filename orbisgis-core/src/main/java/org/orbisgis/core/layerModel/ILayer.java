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

import com.vividsolutions.jts.geom.Envelope;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;
import net.opengis.ows_context.LayerType;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Description;

public interface ILayer {

        //Properties index
        public static final String PROP_DESCRIPTION = "description";
        public static final String PROP_VISIBLE = "visible";
        public static final String PROP_STYLES = "styles";
        public static final String PROP_SELECTION = "selection";
        
        
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
                
	void addLayerListener(LayerListener listener);

	void removeLayerListener(LayerListener listener);

	void addLayerListenerRecursively(LayerListener listener);

	void removeLayerListenerRecursively(LayerListener listener);

        
        
        /**
         * Get the value of description
         *
         * @return the value of description
         */
        public Description getDescription();

        /**
         * Set the value of description
         *
         * @param description new value of description
         */
        public void setDescription(Description description);
        
        /**
         * Get the internal identifier of this layer
         * This is not the displayable layer label.
         * Use the localised description title
         * @return 
         */
	String getName();

        /**
         * Set the internal name of the layer
         * @param name
         * @throws LayerException 
         */
	void setName(final String name) throws LayerException;

	void setParent(final ILayer parent) throws LayerException;

	public Set<String> getAllLayersNames();

	boolean isVisible();

	void setVisible(final boolean isVisible) throws LayerException;

	ILayer getParent();

    /**
     * Returns true if and only if we can serialize this layer in a map context.
     * @return
     */
    boolean isSerializable();
                
	/**
	 * Removes the specified child layer.
	 * 
	 * @param layer
	 * @param isMoving
	 * @return the removed layer or null if the layer was not removed. This can
	 *         be because of a listener cancelling the removal or the layer
	 *         doesn't exist, etc.
	 * @throws LayerException
	 */
	ILayer remove(ILayer layer, boolean isMoving) throws LayerException;

	/**
	 * Removes the specified child layer.
	 * 
	 * @param layer
	 * @return the removed layer or null if the layer was not removed. This can
	 *         be because of a listener cancelling the removal or the layer
	 *         doesn't exist, etc.
	 * @throws LayerException
	 */
	ILayer remove(ILayer layer) throws LayerException;

	/**
	 * Removes the specified child layer.
	 * 
	 * @param layerName
	 * @return the removed layer or null if the layer was not removed. This can
	 *         be because of a listener cancelling the removal or the layer
	 *         doesn't exist, etc.
	 * @throws LayerException
	 */
	ILayer remove(String layerName) throws LayerException;

        /**
         * Adds a child to this {@code ILayer}.
         * @param layer
         * @throws LayerException If this can't accept a child.
         */
	void addLayer(ILayer layer) throws LayerException;

        /**
         * Adds a child to this {@code ILayer}. This method may behave differently
         * if {@code layer} is a layer being moved or not.
         * @param layer
         * @param isMoving
         * @throws LayerException
         */
	void addLayer(ILayer layer, boolean isMoving) throws LayerException;

	/**
	 * Gets the layer with the specified name. It searches in all the subtree
	 * that has as root this layer. If there is no layer with that name returns
	 * null
	 * 
	 * @param layerName
	 * @return
	 */
	ILayer getLayerByName(String layerName);

	public Envelope getEnvelope();

	boolean acceptsChilds();

	ILayer[] getChildren();

	void insertLayer(ILayer layer, int index) throws LayerException;

	int getIndex(ILayer targetLayer);

	ILayer[] getLayersRecursively();

	ILayer[] getLayerPath();

        /**
         * Inserts this in {@code layer} at index {@code index}.
         * @param layer
         * @param index
         * @throws LayerException
         */
	void moveTo(ILayer layer, int index) throws LayerException;

	void moveTo(ILayer layer) throws LayerException;

	void open() throws LayerException;

	void close() throws LayerException;

	void insertLayer(ILayer layer, int index, boolean isMoving)
			throws LayerException;

	int getLayerCount();

	/**
	 * Gets the specified child layer
	 * 
	 * @param index
	 * @return
	 */
	public ILayer getLayer(final int index);

	/**
	 * Gets all the raster layers in the tree under this layer
	 * 
	 * @return
	 * @throws DriverException
	 */
	ILayer[] getRasterLayers() throws DriverException;

	/**
	 * Gets all the vectorial layers in the tree under this layer
	 * 
	 * @return
	 * @throws DriverException
	 */
	ILayer[] getVectorLayers() throws DriverException;

	/**
	 * Returns true if the default spatial field of this layer is of type
	 * {@link Type}.RASTER. Return false if the layer is a collection of layers
	 * or it doesn't contain any spatial field
	 * 
	 * @return
	 * @throws DriverException
	 */
	boolean isRaster() throws DriverException;

	/**
	 * Returns true if the default spatial field of this layer is of type
	 * {@link Type}.GEOMETRY. Return false if the layer is a collection of
	 * layers or it doesn't contain any spatial field
	 * 
	 * @return
	 * @throws DriverException
	 */
	boolean isVectorial() throws DriverException;

	/**
	 * Returns true if this layer represents a Stream source.
	 * 
         * @return
         * @throws DriverException  
	 */
	boolean isStream() throws DriverException;

	/**
	 * Returns a {@link DataSource} to access the source of this layer
	 * 
	 * @return A DataSource or null if this layer is not backed
	 *         up by a DataSource (Layer collections and WMS
	 *         layers, for example)
	 */
	DataSource getDataSource();

        /**
         * Gets the {@code List} of SE styles that are used to define the
         * symbologies associated to the current {@code ILayer}.
         * @return
         */
        List<Style> getStyles();

        /**
         *Sets the {@code List} of SE styles that are used to define the
         * symbologies associated to the current {@code ILayer}.
         * @param fts
         */
        void setStyles(List<Style> fts);

        /**
         * Gets the {@code i}th {@code Style} that is used to define the
         * symbology associated to the current {@code ILayer}.
         * @return
         */
        Style getStyle(int i);

        /**
         * Sets the {@code i}th {@code Style} that is used to define the
         * symbology associated to the current {@code ILayer}.
         * @param i
         * @param s
         */
        void setStyle(int i, Style s);

        /**
         * Adds a {@code Style} instance at the end of the list of associated
         * {@code Style}s.
         * @param style
         */
        public void addStyle(Style style);

        /**
         * Adds a {@code Style} instance at the ith position of the list of
         * associated {@code Style}s.
         * @param style
         */
        public void addStyle(int i, Style style);

        /**
         * Gets the index of {@code s} in this {@code ILayer}, or {@code -1} if
         * {@code s} is not associated to this.
         * @param s
         * @return
         */
        public int indexOf(Style s);

	/**
	 * If isRaster is true returns the first raster in the layer DataSource.
	 * Otherwise it throws an {@link UnsupportedOperationException}. The method
	 * is just a shortcut for getDataSource().getRaster(0)
	 * 
	 * @return
	 * @throws DriverException
	 * @throws UnsupportedOperationException
	 */
	GeoRaster getRaster() throws DriverException, UnsupportedOperationException;

	/**
	 * Gets an array of the selected rows
	 * 
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this layer doesn't support selection
	 */
	Set<Integer> getSelection() throws UnsupportedOperationException;

	/**
	 * Sets the array of the selected rows
	 * 
	 * @param newSelection
	 * @throws UnsupportedOperationException
	 *             If this layer doesn't support selection
	 */
	void setSelection(Set<Integer> newSelection) throws UnsupportedOperationException;

        /**
         * Gets the list of all the {@code Rule} embedded in the {@code Style}
         * associated to this {@code ILayer}.
         * @return
         * @throws DriverException
         */
	List<Rule> getRenderingRule() throws DriverException;

        /**
         * Removes s from the styles associated to this layer.
         */
        void removeStyle(Style s);

}
