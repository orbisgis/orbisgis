package org.orbisgis.geocognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGeocognitionElement implements
		GeocognitionElement {
	private ArrayList<GeocognitionElementListener> listeners = new ArrayList<GeocognitionElementListener>();
	private GeocognitionElement parent = null;

	protected void fireContentChanged() {
		for (GeocognitionElementListener listener : listeners) {
			listener.contentChanged(this);
		}
	}

	protected void fireSave() {
		for (GeocognitionElementListener listener : listeners) {
			listener.saved(this);
		}
	}

	@Override
	public void addElementListener(GeocognitionElementListener listener) {
		listeners.add(listener);
	}

	@Override
	public boolean removeElementListener(GeocognitionElementListener listener) {
		return listeners.remove(listener);
	}

	protected void fireIdChanged() {
		for (GeocognitionElementListener listener : listeners) {
			listener.idChanged(this);
		}
	}

	public void setParent(GeocognitionElement parent) {
		this.parent = parent;
	}

	public GeocognitionElement getParent() {
		return parent;
	}

	@Override
	public String getIdPath() {
		String path = getId();
		GeocognitionElement p = parent;
		// Don't add the root name
		while (p != null) {
			String id = p.getId();
			path = id + "/" + path;
			p = p.getParent();
		}

		return path;
	}

	public abstract void elementRemoved(GeocognitionElement oldParent);

	@Override
	public Map<String, String> getProperties() {
		return new HashMap<String, String>();
	}

}
