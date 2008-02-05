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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.orbisgis.CollectionUtils;
import org.orbisgis.geoview.images.IconLoader;

public abstract class ALayer implements ILayer {
	private String name;

	private ILayer parent;

	private ArrayList<LayerListener> listeners;

	public ALayer(final String name) {
		this.name = name;
		listeners = new ArrayList<LayerListener>();
	}

	/* getters and setters */
	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#getParent()
	 */
	public ILayer getParent() {
		return parent;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#setParent()
	 */
	public void setParent(final ILayer parent) {
		this.parent = parent;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @throws LayerException
	 * @see org.orbisgis.geoview.layerModel.ILayer#setName(java.lang.String)
	 */
	public void setName(final String name) throws LayerException {
		final Set<String> allLayersNames = getRoot().getAllLayersNames();
		allLayersNames.remove(getName());
		this.name = provideNewLayerName(name, allLayersNames);
		fireNameChanged();
	}

	public Set<String> getAllLayersNames() {
		Set<String> result = new HashSet<String>();

		result.add(getName());
		if (this instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) this;
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

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#getIcon()
	 */
	public Icon getIcon() {
		return IconLoader.getIcon("map.png");
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
		try {
			layer.insertLayer(this, index, true);
		} catch (CRSException e) {
			throw new RuntimeException(e);
		}
		fireLayerMovedEvent(oldParent, this);
	}

	public void moveTo(ILayer layer) throws LayerException {
		if (CollectionUtils.contains(getLayersRecursively(), layer)) {
			throw new LayerException("Cannot move a layer to its child");
		}
		ILayer oldParent = getParent();
		oldParent.remove(this, true);
		try {
			layer.addLayer(this, true);
		} catch (CRSException e) {
			throw new RuntimeException(e);
		}
		fireLayerMovedEvent(oldParent, this);
	}

	private void fireNameChanged() {
		if (null != listeners) {
			for (LayerListener listener : listeners) {
				listener.nameChanged(new LayerListenerEvent(this));
			}
		}
	}

	protected void fireVisibilityChanged() {
		if (null != listeners) {
			for (LayerListener listener : listeners) {
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

	protected void fireStyleChanged() {
		for (LayerListener listener : listeners) {
			listener.styleChanged(new LayerListenerEvent(this));
		}
	}

	protected void fireLayerAddedEvent(ILayer[] added) {
		for (LayerListener listener : listeners) {
			listener.layerAdded(new LayerCollectionEvent(this, added));
		}
	}

	protected void fireLayerRemovedEvent(ILayer[] added) {
		for (LayerListener listener : listeners) {
			listener.layerRemoved(new LayerCollectionEvent(this, added));
		}
	}

}