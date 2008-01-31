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
/**
 *
 */
package org.orbisgis.geoview.layerModel;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public abstract class BasicLayer extends ALayer {
	private CoordinateReferenceSystem coordinateReferenceSystem;

	private boolean isVisible = true;

	public BasicLayer(final String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name);
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#setCoordinateReferenceSystem(org.opengis.referencing.crs.CoordinateReferenceSystem)
	 */
	public void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem) {
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		fireVisibilityChanged();
	}

	public void addLayer(ILayer layer) throws CRSException {
		throw new IllegalArgumentException("This layer cannot have children");
	}

	public ILayer remove(ILayer layer) {
		throw new IllegalArgumentException("This layer does not have children");
	}

	public ILayer remove(String layerName) {
		throw new IllegalArgumentException("This layer does not have children");
	}

	public boolean acceptsChilds() {
		return false;
	}

	public ILayer[] getChildren() {
		return new ILayer[0];
	}

	public int getIndex(ILayer targetLayer) {
		return -1;
	}

	public void insertLayer(ILayer layer, int index) throws CRSException {
		throw new IllegalArgumentException("This layer cannot have children");
	}

	public void addLayerListenerRecursively(LayerListener listener) {
		addLayerListener(listener);
	}

	public void removeLayerListenerRecursively(LayerListener listener) {
		removeLayerListener(listener);
	}

	public void addLayer(ILayer layer, boolean isMoving) throws LayerException {
		throw new IllegalArgumentException("This layer cannot have children");
	}

	public ILayer remove(ILayer layer, boolean isMoving) throws LayerException {
		throw new IllegalArgumentException("This layer cannot have children");
	}

	public void insertLayer(ILayer layer, int index, boolean isMoving)
			throws LayerException, CRSException {
		throw new IllegalArgumentException("This layer cannot have children");
	}

	public int getLayerCount() {
		return 0;
	}

	public ILayer getLayer(final int index) {
		throw new ArrayIndexOutOfBoundsException(
				"This layer doesn't contain any child");
	}

	public ILayer getLayerByName(String layerName) {
		return null;
	}

	public ILayer[] getRasterLayers() {
		return new ILayer[0];
	}

	public ILayer[] getVectorLayers() {
		return new ILayer[0];
	}
}