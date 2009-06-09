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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.orbisgis.utils.CollectionUtils;

public abstract class AbstractLayer implements ILayer {
	private String name;

	private ILayer parent;

	protected ArrayList<LayerListener> listeners = new ArrayList<LayerListener>();

	public AbstractLayer(final String name) {
		this.name = name;
		listeners = new ArrayList<LayerListener>();
	}

	/* getters and setters */
	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#getParent()
	 */
	public ILayer getParent() {
		return parent;
	}

	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#setParent()
	 */
	public void setParent(final ILayer parent) {
		this.parent = parent;
	}

	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @throws LayerException
	 * @see org.orbisgis.core.layerModel.ILayer#setName(java.lang.String)
	 */
	public void setName(final String name) throws LayerException {
		final Set<String> allLayersNames = getRoot().getAllLayersNames();
		allLayersNames.remove(getName());
		this.name = provideNewLayerName(name, allLayersNames);
		fireNameChanged();
	}

	public Set<String> getAllLayersNames() {
		final Set<String> result = new HashSet<String>();

		result.add(getName());
		if (this instanceof LayerCollection) {
			final LayerCollection lc = (LayerCollection) this;
			if (null != lc.getLayerCollection()) {
				for (ILayer layer : lc.getChildren()) {
					if (layer instanceof LayerCollection) {
						result.addAll(layer.getAllLayersNames());
					} else {
						result.add(layer.getName());
					}
				}
			}
		}
		return result;
	}

	private String provideNewLayerName(final String name,
			final Set<String> allLayersNames) {
		String tmpName = name;
		if (allLayersNames.contains(tmpName)) {
			int i = 1;
			while (allLayersNames.contains(tmpName + "_" + i)) {
				i++;
			}
			tmpName += "_" + i;
		}
		allLayersNames.add(tmpName);
		return tmpName;
	}

	public ILayer getRoot() {
		ILayer root = this;
		while (null != root.getParent()) {
			root = root.getParent();
		}
		return root;
	}

	public void addLayerListener(LayerListener listener) {
		listeners.add(listener);
	}

	public void removeLayerListener(LayerListener listener) {
		listeners.remove(listener);
	}

	public ILayer[] getLayersRecursively() {
		ArrayList<ILayer> ret = new ArrayList<ILayer>();
		ILayer[] children = getChildren();
		for (ILayer layer : children) {
			ret.add(layer);
			ILayer[] layersRecursively = layer.getLayersRecursively();
			for (ILayer layer2 : layersRecursively) {
				ret.add(layer2);
			}
		}

		return ret.toArray(new ILayer[0]);
	}

	public ILayer[] getLayerPath() {
		ArrayList<ILayer> path = new ArrayList<ILayer>();
		ILayer current = this;
		while (current != null) {
			path.add(current);
			current = current.getParent();
		}

		// Now we must reverse the order
		ArrayList<ILayer> path2 = new ArrayList<ILayer>();
		int l = path.size();
		for (int i = 0; i < l; i++) {
			path2.add(i, path.get(l - i - 1));
		}

		return path2.toArray(new ILayer[0]);
	}

	public void moveTo(ILayer layer, int index) throws LayerException {
		ILayer oldParent = getParent();
		oldParent.remove(this, true);
		layer.insertLayer(this, index, true);
		fireLayerMovedEvent(oldParent, this);
	}

	public void moveTo(ILayer layer) throws LayerException {
		if (CollectionUtils.contains(getLayersRecursively(), layer)) {
			throw new LayerException("Cannot move a layer to its child");
		}
		ILayer oldParent = getParent();
		oldParent.remove(this, true);
		layer.addLayer(this, true);
		fireLayerMovedEvent(oldParent, this);
	}

	private void fireNameChanged() {
		if (null != listeners) {
			for (LayerListener listener : listeners) {
				listener.nameChanged(new LayerListenerEvent(this));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireVisibilityChanged() {
		if (null != listeners) {
			ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
					.clone();
			for (LayerListener listener : l) {
				listener.visibilityChanged(new LayerListenerEvent(this));
			}
		}
	}

	private void fireLayerMovedEvent(ILayer parent, ILayer layer) {
		for (LayerListener listener : listeners) {
			listener.layerMoved(new LayerCollectionEvent(parent,
					new ILayer[] { layer }));
		}

	}

	@SuppressWarnings("unchecked")
	protected void fireStyleChanged() {
		ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
				.clone();
		for (LayerListener listener : l) {
			listener.styleChanged(new LayerListenerEvent(this));
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireLayerAddedEvent(ILayer[] added) {
		ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
				.clone();
		for (LayerListener listener : l) {
			listener.layerAdded(new LayerCollectionEvent(this, added));
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireLayerRemovedEvent(ILayer[] removed) {
		ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
				.clone();
		for (LayerListener listener : l) {
			listener.layerRemoved(new LayerCollectionEvent(this, removed));
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean fireLayerRemovingEvent(ILayer[] toRemove) {
		ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
				.clone();
		for (LayerListener listener : l) {
			if (!listener
					.layerRemoving(new LayerCollectionEvent(this, toRemove))) {
				return false;
			}
		}

		return true;
	}

}