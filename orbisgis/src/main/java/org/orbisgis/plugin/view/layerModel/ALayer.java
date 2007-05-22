/**
 *
 */
package org.orbisgis.plugin.view.layerModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class ALayer implements ILayer {
	private String name;

	private ILayer parent;

	private ArrayList<LayerListener> listeners;

	public ALayer(final String name) {
		this.name = name;
		listeners = new ArrayList<LayerListener>();
	}

	/* getters and setters */
	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#getParent()
	 */
	public ILayer getParent() {
		return parent;
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#setParent()
	 */
	public void setParent(final ILayer parent) {
		this.parent = parent;
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#getName()
	 */
	public String getName() {
		return name;
	}

	public void setNameWithoutChecking(final String name) {
		this.name = name;
		fireNameChanged();
	}

	public void setName(final String name, final Set<String> allLayersNames) {
		this.name = provideNewLayerName(name, allLayersNames);
		fireNameChanged();
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#setName(java.lang.String)
	 */
	public void setName(final String name) {
		final Set<String> allLayersNames = getRoot().getAllLayersNames();
		allLayersNames.remove(getName());
		this.name = provideNewLayerName(name, allLayersNames);
		fireNameChanged();
	}

	public Set<String> getAllLayersNames() {
		Set<String> result = new HashSet<String>();

		result.add(getName());
		if (this instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) this;
			if (null != lc.getLayerCollection()) {
				for (ILayer layer : lc.getLayers()) {
					if (layer instanceof LayerCollection) {
						result.addAll(layer.getAllLayersNames());
					} else {
						result.add(layer.getName());
					}
				}
			}
		}
		return result;
	}

	public String provideNewLayerName(final String name,
			final Set<String> allLayersNames) {
		String tmpName = name;
		if (allLayersNames.contains(tmpName)) {
			int i = 1;
			while (allLayersNames.contains(tmpName + "_" + i)) {
				i++;
			}
			tmpName += "_" + i;
		}
		allLayersNames.add(tmpName);
		return tmpName;
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#getIcon()
	 */
	public Icon getIcon() {
		return new ImageIcon(getClass().getResource(
				getClass().getSimpleName() + ".png"));
	}

	public ILayer getRoot() {
		ILayer root = this;
		while (null != root.getParent()) {
			root = root.getParent();
		}
		return root;
	}

	public void addLayerListener(LayerListener listener) {
		listeners.add(listener);
	}

	public void removeLayerListener(LayerListener listener) {
		listeners.remove(listener);
	}

	private void fireNameChanged() {
		if (null != listeners) {
			for (LayerListener listener : listeners) {
				listener.nameChanged(new LayerListenerEvent(this));
			}
		}
	}

	protected void fireVisibilityChanged() {
		if (null != listeners) {
			for (LayerListener listener : listeners) {
				listener.visibilityChanged(new LayerListenerEvent(this));
			}
		}
	}
}