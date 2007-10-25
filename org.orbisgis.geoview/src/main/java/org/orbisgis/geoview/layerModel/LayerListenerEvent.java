package org.orbisgis.geoview.layerModel;

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
