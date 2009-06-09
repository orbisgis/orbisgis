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
package org.orbisgis.core.layerModel;

import java.util.Set;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.layerModel.persistence.LayerType;

import com.vividsolutions.jts.geom.Envelope;

public interface ILayer {

	void addLayerListener(LayerListener listener);

	void removeLayerListener(LayerListener listener);

	void addLayerListenerRecursively(LayerListener listener);

	void removeLayerListenerRecursively(LayerListener listener);

	String getName();

	void setName(final String name) throws LayerException;

	void setParent(final ILayer parent) throws LayerException;

	public Set<String> getAllLayersNames();

	boolean isVisible();

	void setVisible(final boolean isVisible) throws LayerException;

	ILayer getParent();

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

	void addLayer(ILayer layer) throws LayerException;

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

	CoordinateReferenceSystem getCoordinateReferenceSystem();

	void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem)
			throws LayerException;

	public Envelope getEnvelope();

	boolean acceptsChilds();

	ILayer[] getChildren();

	void insertLayer(ILayer layer, int index) throws LayerException;

	int getIndex(ILayer targetLayer);

	ILayer[] getLayersRecursively();

	ILayer[] getLayerPath();

	void moveTo(ILayer layer, int index) throws LayerException;

	void moveTo(ILayer layer) throws LayerException;

	void open() throws LayerException;

	void close() throws LayerException;

	void insertLayer(ILayer layer, int index, boolean isMoving)
			throws LayerException;

	int getLayerCount();

	/**
	 * Gets the status of this object as a xml object
	 *
	 * @return
	 */
	LayerType saveLayer();

	/**
	 * Sets the status of the layer from a xml object
	 *
	 * @param layer
	 * @throws LayerException
	 *             If the status cannot be set
	 */
	void restoreLayer(LayerType layer) throws LayerException;

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
	 * Returns true if this layer represents a WMS source
	 *
	 * @return
	 */
	boolean isWMS();

	/**
	 * Returns a {@link DataSource} to access the source of this layer
	 *
	 * @return A DataSource or null if this layer is not backed up by a
	 *         DataSource (Layer collections and WMS layers, for example)
	 */
	SpatialDataSourceDecorator getDataSource();

	/**
	 * Gets the legend used to draw the default spatial field in this layer if
	 * it is of type raster.
	 *
	 * @return
	 * @throws DriverException
	 *             If there is some problem accessing the default spatial field
	 * @throws UnsupportedOperationException
	 *             If the spatial field is not raster but vector
	 */
	RasterLegend[] getRasterLegend() throws DriverException,
			UnsupportedOperationException;

	/**
	 * Gets the legends used to draw the default spatial field in this layer if
	 * it is of type vector.
	 *
	 * @return
	 * @throws DriverException
	 *             If there is some problem accessing the default spatial field
	 * @throws UnsupportedOperationException
	 *             If the spatial field is not vector but raster
	 */
	Legend[] getVectorLegend() throws DriverException,
			UnsupportedOperationException;

	/**
	 * Sets the legend used to draw the default spatial field in this layer
	 *
	 * @param legends
	 * @throws DriverException
	 *             If there is some problem accessing the contents of the layer
	 */
	void setLegend(Legend... legends) throws DriverException;

	/**
	 * Gets the legend used to draw the specified vector field in this layer
	 *
	 * @return
	 * @throws IllegalArgumentException
	 *             If the specified name does not exist or it's not of type
	 *             vector
	 * @throws DriverException
	 */
	Legend[] getVectorLegend(String fieldName) throws IllegalArgumentException,
			DriverException;

	/**
	 * Gets the legend used to draw the specified raster field in this layer
	 *
	 * @return
	 * @throws IllegalArgumentException
	 *             If the specified name does not exist or it's not of type
	 *             raster
	 * @throws DriverException
	 */
	RasterLegend[] getRasterLegend(String fieldName)
			throws IllegalArgumentException, DriverException;

	/**
	 * Sets the legend used to draw the specified spatial field in this layer
	 *
	 * @param legends
	 * @throws IllegalArgumentException
	 *             If the specified name does not exist
	 * @throws DriverException
	 *             If there is some problem accessing the contents of the layer
	 */
	void setLegend(String fieldName, Legend... legends)
			throws IllegalArgumentException, DriverException;

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
	 * Gets an object to manage the WMS contents in this layer.
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this layer is not a WMS layer. This is {@link #isWMS()}
	 *             returns false
	 */
	WMSConnection getWMSConnection() throws UnsupportedOperationException;

	/**
	 * Gets an array of the selected rows
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this layer doesn't support selection
	 */
	int[] getSelection() throws UnsupportedOperationException;

	/**
	 * Sets the array of the selected rows
	 *
	 * @param newSelection
	 * @throws UnsupportedOperationException
	 *             If this layer doesn't support selection
	 */
	void setSelection(int[] newSelection) throws UnsupportedOperationException;

	/**
	 * Gets the legend to perform the rendering. The actual class of the
	 * returned legends may not be the same of those set by setLegend methods
	 *
	 * @return
	 * @throws DriverException
	 */
	Legend[] getRenderingLegend() throws DriverException;

}