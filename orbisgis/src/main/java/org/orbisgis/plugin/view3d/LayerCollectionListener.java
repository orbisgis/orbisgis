package org.orbisgis.plugin.view3d;

import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;

public class LayerCollectionListener implements
		org.orbisgis.plugin.view.layerModel.LayerCollectionListener {

	private LayerListener layerListener = null;

	private Renderer3D renderer = null;

	public LayerCollectionListener(SimpleCanvas3D simpleCanvas) {
		layerListener = new LayerListener();
		renderer = new Renderer3D(simpleCanvas);
	}

	public void layerAdded(LayerCollectionEvent listener) {
		for (ILayer layer : listener.getAffected()) {
			layer.addLayerListener(layerListener);
			renderer.processLayer(layer);
		}
	}

	public void layerMoved(LayerCollectionEvent listener) {
		// TODO Auto-generated method stub
	}

	public void layerRemoved(LayerCollectionEvent listener) {
		// TODO : undraw
		for (ILayer layer : listener.getAffected()) {
			layer.setVisible(false);
			layer.removeLayerListener(layerListener);
			
		}
	}

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
			renderer.processLayerVisibility(e.getAffectedLayer());
		}

	}

}
