/**
 *
 */
package org.orbisgis.geoview.layerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gdms.data.types.NullCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.persistence.LayerCollectionType;
import org.orbisgis.geoview.persistence.LayerType;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class LayerCollection extends ALayer {
	private List<ILayer> layerCollection;

	LayerCollection(String name) {
		super(name);
		layerCollection = new ArrayList<ILayer>();
	}

	List<ILayer> getLayerCollection() {
		return layerCollection;
	}

	public int getIndex(ILayer layer) {
		return layerCollection.indexOf(layer);
	}

	public ILayer getLayer(final int index) {
		return layerCollection.get(index);
	}

	public boolean containsLayerName(final String layerName) {
		return getAllLayersNames().contains(layerName);
	}

	private void setNamesRecursively(final ILayer layer,
			final Set<String> allLayersNames) throws LayerException {
		layer.setName(provideNewLayerName(layer.getName(), allLayersNames));
		if (layer instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) layer;
			if (null != lc.getLayerCollection()) {
				for (ILayer layerItem : lc.getChildren()) {
					setNamesRecursively(layerItem, allLayersNames);
				}
			}
		}
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

	public void put(final ILayer layer) throws CRSException, LayerException {
		put(layer, false);
	}

	// Allows to put a layer at a specific index
	public void insertLayer(final ILayer layer, int index) throws CRSException,
			LayerException {
		insertLayer(layer, index, false);
	}

	/**
	 * Removes the layer from the collection
	 *
	 * @param layerName
	 * @return the layer removed or null if the layer does not exists
	 * @throws LayerException
	 *
	 */
	public ILayer remove(final String layerName) throws LayerException {
		for (int i = 0; i < size(); i++) {
			if (layerName.equals(layerCollection.get(i).getName())) {
				return remove(layerCollection.get(i));
			}
		}
		return null;
	}

	public ILayer[] getChildren() {
		if (null != layerCollection) {
			ILayer[] result = new ILayer[size()];
			return layerCollection.toArray(result);
		} else {
			return null;
		}
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
	 * @see org.orbisgis.geoview.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		for (ILayer layer : getChildren()) {
			if (layer.isVisible()) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @throws LayerException
	 * @see org.orbisgis.geoview.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) throws LayerException {
		for (ILayer layer : getChildren()) {
			layer.setVisible(isVisible);
		}
		fireVisibilityChanged();
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return (0 < size()) ? getLayer(0).getCoordinateReferenceSystem()
				: NullCRS.singleton;
	}

	/**
	 *
	 * @throws LayerException
	 * @see org.orbisgis.geoview.layerModel.ILayer#setCoordinateReferenceSystem(org.opengis.referencing.crs.CoordinateReferenceSystem)
	 */
	public void setCoordinateReferenceSystem(
			final CoordinateReferenceSystem coordinateReferenceSystem)
			throws LayerException {
		for (ILayer layer : getChildren()) {
			layer.setCoordinateReferenceSystem(coordinateReferenceSystem);
		}
	}

	public static void processLayersLeaves(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			ILayer lc = (ILayer) root;
			ILayer[] layers = lc.getChildren();
			for (ILayer layer : layers) {
				processLayersLeaves(layer, action);
			}
		} else {
			action.action(root);
		}
	}

	public static void processLayersNodes(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			ILayer lc = (ILayer) root;
			ILayer[] layers = lc.getChildren();
			for (ILayer layer : layers) {
				processLayersNodes(layer, action);
			}
		}
		action.action(root);
	}

	public void setStyle(Style style) {
	}

	private class GetEnvelopeLayerAction implements ILayerAction {
		private Envelope globalEnvelope;

		public void action(ILayer layer) {
			if (null == globalEnvelope) {
				globalEnvelope = new Envelope(layer.getEnvelope());
			} else {
				globalEnvelope.expandToInclude(layer.getEnvelope());
			}
		}

		public Envelope getGlobalEnvelope() {
			return globalEnvelope;
		}
	}

	public Envelope getEnvelope() {
		final GetEnvelopeLayerAction tmp = new GetEnvelopeLayerAction();
		processLayersLeaves(this, tmp);
		return tmp.getGlobalEnvelope();
	}

	private static class MyILayerAction implements ILayerAction {
		private int numberOfLeaves = 0;

		public void action(ILayer layer) {
			numberOfLeaves++;
		}

		public int getNumberOfLeaves() {
			return numberOfLeaves;
		}
	}

	public static int getNumberOfLeaves(final ILayer root) {
		MyILayerAction ila = new MyILayerAction();
		LayerCollection.processLayersLeaves(root, ila);
		return ila.getNumberOfLeaves();
	}

	public ILayer remove(ILayer layer) throws LayerException {
		return remove(layer, false);
	}

	public boolean acceptsChilds() {
		return true;
	}

	public void addLayerListenerRecursively(LayerListener listener) {
		this.addLayerListener(listener);
		for (ILayer layer : layerCollection) {
			layer.addLayerListenerRecursively(listener);
		}
	}

	public void removeLayerListenerRecursively(LayerListener listener) {
		this.removeLayerListener(listener);
		for (ILayer layer : layerCollection) {
			layer.removeLayerListenerRecursively(listener);
		}
	}

	public void close() throws LayerException {
		for (ILayer layer : layerCollection) {
			layer.close();
		}
	}

	public void open() throws LayerException {
		for (ILayer layer : layerCollection) {
			layer.open();
		}
	}

	public void put(ILayer layer, boolean isMoving) throws LayerException,
			CRSException {
		if (null != layer) {
			if (isMoving) {
				layerCollection.add(layer);
				layer.setParent(this);
			} else {
				if (0 < size()) {
					if (!layer.getCoordinateReferenceSystem().equals(
							getCoordinateReferenceSystem())) {
						throw new CRSException(
								"new layer don't share LayerCollection's CRS");
					}
				}
				setNamesRecursively(layer, getRoot().getAllLayersNames());
				layerCollection.add(layer);
				layer.setParent(this);
				fireLayerAddedEvent(new ILayer[] { layer });
			}
		}
	}

	public ILayer remove(ILayer layer, boolean isMoving) throws LayerException {
		if (layerCollection.remove(layer)) {
			if (!isMoving) {
				fireLayerRemovedEvent(new ILayer[] { layer });
			}
			return layer;
		} else {
			return null;
		}
	}

	public void insertLayer(ILayer layer, int index, boolean isMoving)
			throws LayerException, CRSException {
		if (null != layer) {
			if (isMoving) {
				layerCollection.add(index, layer);
				layer.setParent(this);
			} else {
				if (0 < size()) {
					if (!layer.getCoordinateReferenceSystem().equals(
							getCoordinateReferenceSystem())) {
						throw new CRSException(
								"new layer don't share LayerCollection's CRS");
					}
				}
				setNamesRecursively(layer, getRoot().getAllLayersNames());
				layerCollection.add(index, layer);
				layer.setParent(this);
				fireLayerAddedEvent(new ILayer[] { layer });
			}
		}

	}

	public int getLayerCount() {
		return layerCollection.size();
	}

	public LayerType getStatus() {
		LayerCollectionType xmlLayer = new LayerCollectionType();
		xmlLayer.setName(getName());
		for (ILayer child : layerCollection) {
			LayerType xmlChild = child.getStatus();
			xmlLayer.getLayer().add(xmlChild);
		}

		return xmlLayer;
	}

}