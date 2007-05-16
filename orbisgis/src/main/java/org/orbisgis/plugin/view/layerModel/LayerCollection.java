/**
 *
 */
package org.orbisgis.plugin.view.layerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class LayerCollection extends ALayer {
	private List<ILayer> layerCollection;

	private List<LayerCollectionListener> lclisteners;

	public LayerCollection(String name) {
		super(name);
		layerCollection = new ArrayList<ILayer>();
		lclisteners = new ArrayList<LayerCollectionListener>();
	}

	public List<ILayer> getLayerCollection() {
		return layerCollection;
	}

	public void addCollectionListener(LayerCollectionListener listener) {
		lclisteners.add(listener);
	}

	public void removeCollectionListener(LayerCollectionListener listener) {
		lclisteners.remove(listener);
	}

	private void fireLayerAddedEvent(ILayer[] added) {
		for (LayerCollectionListener listener : lclisteners) {
			listener.layerAdded(new LayerCollectionEvent(this, added));
		}
	}

	private void fireLayerRemovedEvent(ILayer[] added) {
		for (LayerCollectionListener listener : lclisteners) {
			listener.layerRemoved(new LayerCollectionEvent(this, added));
		}
	}

	public ILayer getLayerByIndex(final int index) {
		return layerCollection.get(index);
	}

	public boolean containsLayerName(final String layerName) {
		return getAllLayersNames().contains(layerName);
	}

	private void setNamesRecursively(final ILayer layer,
			final Set<String> allLayersNames) {
		layer.setName(layer.getName(), allLayersNames);
		if (layer instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) layer;
			if (null != lc.getLayerCollection()) {
				for (ILayer layerItem : lc.getLayers()) {
					setNamesRecursively(layerItem, allLayersNames);
				}
			}
		}
	}

	public ILayer put(final ILayer layer) throws CRSException {
		if (null != layer) {
			if (0 < size()) {
				if (!layer.getCoordinateReferenceSystem().toWKT().equals(
						getCoordinateReferenceSystem().toWKT())) {
					throw new CRSException(
							"new layer don't share LayerCollection's CRS");
				}
			}
			setNamesRecursively(layer, getRoot().getAllLayersNames());
			layerCollection.add(layer);
			layer.setParent(this);
			fireLayerAddedEvent(new ILayer[] { layer });
		}
		return layer;
	}

	/**
	 * Removes the layer from the collection
	 * 
	 * @param layerName
	 * @return the layer removed or null if the layer does not exists
	 * 
	 */
	public ILayer remove(final String layerName) {
		for (int i = 0; i < size(); i++) {
			if (layerName.equals(layerCollection.get(i).getName())) {
				ILayer l = layerCollection.remove(i);
				fireLayerRemovedEvent(new ILayer[] { l });
				return l;
			}
		}
		return null;
	}

	public ILayer[] getLayers() {
		if (null != layerCollection) {
			ILayer[] result = new ILayer[size()];
			return layerCollection.toArray(result);
		} else {
			return null;
		}
	}

	public void putAll(List<ILayer> layerList) throws CRSException {
		for (ILayer layer : layerList)
			put(layer);
		ILayer[] removed = layerList.toArray(new ILayer[0]);
		fireLayerAddedEvent(removed);
	}

	public void removeAll() {
		ILayer[] removed = layerCollection.toArray(new ILayer[0]);
		layerCollection.clear();
		fireLayerRemovedEvent(removed);
	}

	public int size() {
		return layerCollection.size();
	}

	/**
	 * 
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		for (ILayer layer : getLayers()) {
			if (layer.isVisible())
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) {
		for (ILayer layer : getLayers()) {
			layer.setVisible(isVisible);
		}
		fireVisibilityChanged();
	}

	/**
	 * 
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return (0 < size()) ? getLayerByIndex(0).getCoordinateReferenceSystem()
				: null;
	}

	/**
	 * 
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#setCoordinateReferenceSystem(org.opengis.referencing.crs.CoordinateReferenceSystem)
	 */
	public void setCoordinateReferenceSystem(
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		for (ILayer layer : getLayers()) {
			layer.setCoordinateReferenceSystem(coordinateReferenceSystem);
		}
	}

	public static void processLayersLeaves(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) root;
			ILayer[] layers = lc.getLayers();
			for (ILayer layer : layers) {
				processLayersLeaves(layer, action);
			}
		} else {
			action.action(root);
		}
	}

	public static void processLayersNodes(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) root;
			ILayer[] layers = lc.getLayers();
			for (ILayer layer : layers) {
				processLayersNodes(layer, action);
			}
		}
		action.action(root);
	}
}