package org.orbisgis.plugin.view3d;

import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;

/**
 * This class will listen to layer collection's changes and take the appropriate
 * measures. For each layer added it will register a layerListener. See below
 * for LayerListener details.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class LayerCollectionListener implements
		org.orbisgis.plugin.view.layerModel.LayerCollectionListener {

	// This is our layer listener (used for toggling visibility)
	private LayerListener layerListener = null;

	// The layerRenderer will process (draw) layers if needed
	private LayerRenderer layerRenderer = null;

	public LayerCollectionListener(LayerRenderer renderer) {
		layerListener = new LayerListener();
		this.layerRenderer = renderer;
	}

	public void layerAdded(LayerCollectionEvent listener) {
		// Add and display a layer
		for (ILayer layer : listener.getAffected()) {
			layer.addLayerListener(layerListener);
			layerRenderer.processLayer(layer);
		}
	}

	public void layerMoved(LayerCollectionEvent listener) {
		throw new Error("Operation not supported : moving a layer");
	}

	public void layerRemoved(LayerCollectionEvent listener) {
		// Remove a layer
		for (ILayer layer : listener.getAffected()) {
			layer.setVisible(false);
			layer.removeLayerListener(layerListener);

		}
	}

	/**
	 * This class handles visibility toogling. TODO : implement nameChanged()
	 * and styleChanged() ??
	 * 
	 * @author cerma
	 * 
	 */
	private class LayerListener implements
			org.orbisgis.plugin.view.layerModel.LayerListener {

		public void nameChanged(LayerListenerEvent e) {
			throw new Error("I don't like when you change my name");
		}

		public void styleChanged(LayerListenerEvent e) {
			throw new Error("Changing style not supported yet...");
		}

		public void visibilityChanged(LayerListenerEvent e) {
			// Toogle a layer's visibility.
			// Be aware that this doesn't free memory.
			layerRenderer.processLayerVisibility(e.getAffectedLayer());
		}

	}

}
