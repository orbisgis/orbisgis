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

import java.util.*;
import org.apache.log4j.Logger;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.utils.CollectionUtils;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public abstract class AbstractLayer implements ILayer {
        
        
        protected static final I18n I18N = I18nFactory.getI18n(BeanLayer.class);
        protected static final Logger LOGGER = Logger.getLogger(BeanLayer.class);

	public AbstractLayer() {
		listeners = new ArrayList<LayerListener>();
        }
        
        

	private ILayer parent;

	protected ArrayList<LayerListener> listeners = new ArrayList<LayerListener>();

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

        public Set<String> getAllLayersNames() {
		final Set<String> result = new HashSet<String>();
		result.add(getName());
		return result;
	}

        /*
         * Check that name is not already contained in allLayersNames.
         * If it is in, a new String is created and returned, with the form name_i
         * where i is as small as possible.
         */
	protected String provideNewLayerName(final String name,
			final Set<String> allLayersNames) {
		String tmpName = name;
		if (allLayersNames.contains(tmpName)) {
			int i = 1;
			while (allLayersNames.contains(tmpName + "_" + i)) { //$NON-NLS-1$
				i++;
			}
			tmpName += "_" + i; //$NON-NLS-1$
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

        @Override
	public void addLayerListener(LayerListener listener) {
                if(!listeners.contains(listener)){
                        listeners.add(listener);
                }
	}

        @Override
	public void removeLayerListener(LayerListener listener) {
		listeners.remove(listener);
	}

        
        @Override
	public ILayer[] getLayersRecursively() {
		ArrayList<ILayer> ret = new ArrayList<ILayer>();
		ILayer[] children = getChildren();
		for (ILayer layer : children) {
			ret.add(layer);
			ILayer[] layersRecursively = layer.getLayersRecursively();
                        ret.addAll(Arrays.asList(layersRecursively));
		}

		return ret.toArray(new ILayer[ret.size()]);
	}

        @Override
        public void setStyles(List<Style> fts) {
                this.fireStyleChanged();
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

		return path2.toArray(new ILayer[path2.size()]);
	}

        @Override
	public void moveTo(ILayer layer, int index) throws LayerException {
		ILayer oldParent = getParent();
		oldParent.remove(this, true);
		layer.insertLayer(this, index, true);
		fireLayerMovedEvent(oldParent, this);
	}

	public void moveTo(ILayer layer) throws LayerException {
		if (CollectionUtils.contains(getLayersRecursively(), layer)) {
			throw new LayerException(I18N.tr("Cannot move a layer to its child"));
		}
		ILayer oldParent = getParent();
		oldParent.remove(this, true);
		layer.addLayer(this, true);
		fireLayerMovedEvent(oldParent, this);
	}

	protected void fireNameChanged() {
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
                LayerCollectionEvent evt = new LayerCollectionEvent(parent,
					new ILayer[] { layer });
                LayerCollectionEvent ev2 = new LayerCollectionEvent(layer.getParent(),
					new ILayer[] { layer });
		for (LayerListener listener : listeners) {
			listener.layerMoved(evt);
			listener.layerMoved(ev2);
		}

	}

	protected void fireStyleChanged() {
		ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
				.clone();
		for (LayerListener listener : l) {
			listener.styleChanged(new LayerListenerEvent(this));
		}
	}

	protected void fireLayerAddedEvent(ILayer[] added) {
		ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
				.clone();
		for (LayerListener listener : l) {
			listener.layerAdded(new LayerCollectionEvent(this, added));
		}
	}

	protected void fireLayerRemovedEvent(ILayer[] removed) {
		ArrayList<LayerListener> l = (ArrayList<LayerListener>) listeners
				.clone();
		for (LayerListener listener : l) {
			listener.layerRemoved(new LayerCollectionEvent(this, removed));
		}
	}

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
        
    @Override
    public void addLayer(ILayer layer)  throws LayerException  {
        throw new IllegalArgumentException(I18N.tr("This layer cannot have children")); //$NON-NLS-1$
    }

    @Override
    public ILayer remove(ILayer layer)  throws LayerException {
        throw new IllegalArgumentException(I18N.tr("This layer does not have children")); //$NON-NLS-1$
    }

    @Override
    public ILayer remove(String layerName)  throws LayerException {
        throw new IllegalArgumentException(I18N.tr("This layer does not have children")); //$NON-NLS-1$
    }

    @Override
    public boolean acceptsChilds() {
        return false;
    }

    @Override
    public ILayer[] getChildren() {
        return new ILayer[0];
    }

    @Override
    public int getIndex(ILayer targetLayer) {
        return -1;
    }

    @Override
    public void insertLayer(ILayer layer, int index) throws LayerException {
        throw new IllegalArgumentException(I18N.tr("This layer cannot have children")); //$NON-NLS-1$
    }

    @Override
    public void addLayerListenerRecursively(LayerListener listener) {
        addLayerListener(listener);
    }

    @Override
    public void removeLayerListenerRecursively(LayerListener listener) {
        removeLayerListener(listener);
    }

    @Override
    public void addLayer(ILayer layer, boolean isMoving) throws LayerException {
        throw new IllegalArgumentException(I18N.tr("This layer cannot have children")); //$NON-NLS-1$
    }

    @Override
    public ILayer remove(ILayer layer, boolean isMoving) throws LayerException {
        throw new IllegalArgumentException(I18N.tr("This layer cannot have children")); //$NON-NLS-1$
    }

    @Override
    public void insertLayer(ILayer layer, int index, boolean isMoving)
            throws LayerException {
        throw new IllegalArgumentException(I18N.tr("This layer cannot have children")); //$NON-NLS-1$
    }

    @Override
    public int getLayerCount() {
        return 0;
    }

    @Override
    public ILayer getLayer(final int index) {
        throw new ArrayIndexOutOfBoundsException(
                I18N.tr("This layer doesn't contain any child")); //$NON-NLS-1$
    }

    @Override
    public ILayer getLayerByName(String layerName) {
        return null;
    }

    @Override
    public ILayer[] getRasterLayers() throws DriverException {
        return new ILayer[0];
    }

    @Override
    public ILayer[] getVectorLayers() throws DriverException {
        return new ILayer[0];
    }
}