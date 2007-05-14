package org.orbisgis.plugin.view.layerModel;

public class LayerCollectionEvent {
	private LayerCollection collection;
	private ILayer[] affected;
	public LayerCollectionEvent(LayerCollection collection, ILayer[] affected) {
		super();
		this.collection = collection;
		this.affected = affected;
	}
	public ILayer[] getAffected() {
		return affected;
	}
	public LayerCollection getCollection() {
		return collection;
	}


}
