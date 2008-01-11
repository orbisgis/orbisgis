/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.persistence.LayerType;
import org.orbisgis.geoview.renderer.style.Style;

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

	void put(ILayer layer) throws LayerException, CRSException;

	void put(ILayer layer, boolean isMoving) throws LayerException,
			CRSException;

	CoordinateReferenceSystem getCoordinateReferenceSystem();

	void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem)
			throws LayerException;

	Icon getIcon();

	public void setStyle(Style style);

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

}