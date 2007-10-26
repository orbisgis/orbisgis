package org.orbisgis.geoview.layerModel;

public interface LayerCollectionListener {
	void layerAdded(LayerCollectionEvent e);
	void layerRemoved(LayerCollectionEvent e);
	void layerMoved(LayerCollectionEvent e);
}
