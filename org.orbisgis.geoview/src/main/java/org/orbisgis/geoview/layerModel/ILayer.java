/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.layerModel;

import java.util.Set;

import javax.swing.Icon;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.persistence.LayerType;
import org.orbisgis.geoview.renderer.legend.Legend;

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

	ILayer remove(ILayer layer, boolean isMoving) throws LayerException;

	ILayer remove(ILayer layer) throws LayerException;

	ILayer remove(String layerName) throws LayerException;

	void addLayer(ILayer layer) throws LayerException, CRSException;

	void addLayer(ILayer layer, boolean isMoving) throws LayerException,
			CRSException;

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

	Icon getIcon();

	public Envelope getEnvelope();

	boolean acceptsChilds();

	ILayer[] getChildren();

	void insertLayer(ILayer layer, int index) throws LayerException,
			CRSException;

	int getIndex(ILayer targetLayer);

	ILayer[] getLayersRecursively();

	ILayer[] getLayerPath();

	void moveTo(ILayer layer, int index) throws LayerException;

	void moveTo(ILayer layer) throws LayerException;

	void open() throws LayerException;

	void close() throws LayerException;

	void insertLayer(ILayer layer, int index, boolean isMoving)
			throws LayerException, CRSException;

	int getLayerCount();

	/**
	 * Gets the status of this object as a xml object
	 *
	 * @return
	 */
	LayerType getStatus();

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
	 * Returns a {@link DataSource} to access the source of this layer
	 *
	 * @return
	 */
	SpatialDataSourceDecorator getDataSource();

	/**
	 * Gets the legend used to draw the default spatial field in this layer
	 *
	 * @return
	 * @throws DriverException
	 *             If there is some problem accessing the default spatial field
	 */
	Legend[] getLegend() throws DriverException;

	/**
	 * Sets the legend used to draw the default spatial field in this layer
	 *
	 * @param legends
	 * @throws DriverException
	 *             If there is some problem accessing the contents of the layer
	 */
	void setLegend(Legend... legends) throws DriverException;

	/**
	 * Gets the legend used to draw the specified spatial field in this layer
	 *
	 * @return
	 * @throws IllegalArgumentException
	 *             If the specified name does not exist
	 */
	Legend[] getLegend(String fieldName) throws IllegalArgumentException;

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
	 * Otherwise it throws a {@link RuntimeException}. The method is just a
	 * shortcut for getDataSource().getRaster(0)
	 *
	 * @return
	 * @throws DriverException
	 */
	GeoRaster getRaster() throws DriverException;

}