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

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceManager;
import org.gdms.source.SourceRemovalEvent;
import org.gdms.sql.strategies.TableNotFoundException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;

public abstract class GdmsLayer extends AbstractLayer {

	private CoordinateReferenceSystem coordinateReferenceSystem;
	private boolean isVisible = true;

	private String mainName;
	private SourceListener listener = new NameSourceListener();

	public GdmsLayer(String name,
			CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name);
		this.mainName = name;
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#setCoordinateReferenceSystem(org.opengis.referencing.crs.CoordinateReferenceSystem)
	 */
	public void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem) {
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @throws LayerException
	 * @see org.orbisgis.core.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) throws LayerException {
		this.isVisible = isVisible;
		fireVisibilityChanged();
	}

	public void addLayer(ILayer layer) {
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

	public void insertLayer(ILayer layer, int index) throws LayerException {
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
			throws LayerException {
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

	public ILayer[] getVectorLayers() throws DriverException {
		return new ILayer[0];
	}

	@Override
	public void setName(String name) throws LayerException {
		SourceManager sourceManager = ((DataManager) Services
				.getService(DataManager.class)).getDSF().getSourceManager();

		// Remove previous alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
		if (!name.equals(mainName)) {
			super.setName(name);
			try {
				sourceManager.addName(mainName, name);
			} catch (TableNotFoundException e) {
				throw new RuntimeException("bug!", e);
			} catch (SourceAlreadyExistsException e) {
				throw new LayerException("Source already exists", e);
			}
		} else {
			super.setName(name);
		}
	}

	public void close() throws LayerException {
		SourceManager sourceManager = Services.getService(DataManager.class)
				.getSourceManager();

		sourceManager.removeSourceListener(listener);

		// Remove alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
	}

	@Override
	public void open() throws LayerException {
		SourceManager sourceManager = Services.getService(DataManager.class)
				.getSourceManager();
		sourceManager.addSourceListener(listener);
	}

	protected String getMainName() {
		return mainName;
	}

	private class NameSourceListener implements SourceListener {

		@Override
		public void sourceAdded(SourceEvent e) {
		}

		@Override
		public void sourceNameChanged(SourceEvent e) {
			// If this layer source name was changed
			if (e.getName().equals(mainName)) {
				mainName = e.getNewName();
				// Add alias if necessary
				if (!getName().equals(mainName)
						&& (getName().equals(e.getName()))) {
					SourceManager sourceManager = Services.getService(
							DataManager.class).getSourceManager();
					try {
						// If this layer name was the mainName
						sourceManager.addName(mainName, getName());
					} catch (TableNotFoundException e1) {
						// The table exists since mainName is the new name
						throw new RuntimeException("bug!", e1);
					} catch (SourceAlreadyExistsException e1) {
						// This layer had the old source name so there is no
						// possibility for a conflict to happen
						throw new RuntimeException("bug!", e1);
					}
				}
			}
		}

		@Override
		public void sourceRemoved(SourceRemovalEvent e) {
		}

	}
}