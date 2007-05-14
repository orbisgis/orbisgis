package org.orbisgis.plugin.view.layerModel;

public interface LayerCollectionListener {
	void layerAdded(LayerCollectionEvent listener);
	void layerRemoved(LayerCollectionEvent listener);
	void layerMoved(LayerCollectionEvent listener);
}
