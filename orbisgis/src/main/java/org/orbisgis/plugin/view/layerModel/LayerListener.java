package org.orbisgis.plugin.view.layerModel;

public interface LayerListener {
	void nameChanged(LayerListenerEvent e);

	void visibilityChanged(LayerListenerEvent e);

	void styleChanged(LayerListenerEvent e);
}
