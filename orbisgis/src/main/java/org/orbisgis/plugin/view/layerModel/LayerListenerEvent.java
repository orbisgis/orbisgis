package org.orbisgis.plugin.view.layerModel;

public class LayerListenerEvent {
	private ILayer affectedLayer;

	public LayerListenerEvent(ILayer affectedLayer) {
		super();
		this.affectedLayer = affectedLayer;
	}

	public ILayer getAffectedLayer() {
		return affectedLayer;
	}
}
