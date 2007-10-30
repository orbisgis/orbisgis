package org.orbisgis.geoview.layerModel;

public interface LayerListener {
	void nameChanged(LayerListenerEvent e);

	void visibilityChanged(LayerListenerEvent e);

	void styleChanged(LayerListenerEvent e);

	void layerAdded(LayerCollectionEvent e);

	void layerRemoved(LayerCollectionEvent e);

	void layerMoved(LayerCollectionEvent e);

}
