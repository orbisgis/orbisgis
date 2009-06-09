package org.orbisgis.core.layerModel;

public abstract class LayerListenerAdapter implements LayerListener {

	@Override
	public void layerAdded(LayerCollectionEvent e) {
	}

	@Override
	public void layerMoved(LayerCollectionEvent e) {
	}

	@Override
	public void layerRemoved(LayerCollectionEvent e) {
	}

	@Override
	public boolean layerRemoving(LayerCollectionEvent e) {
		return true;
	}

	@Override
	public void nameChanged(LayerListenerEvent e) {
	}

	@Override
	public void selectionChanged(SelectionEvent e) {
	}

	@Override
	public void styleChanged(LayerListenerEvent e) {
	}

	@Override
	public void visibilityChanged(LayerListenerEvent e) {
	}

}
