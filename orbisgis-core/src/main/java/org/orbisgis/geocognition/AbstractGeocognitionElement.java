package org.orbisgis.geocognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.orbisgis.edition.EditableElementListener;

public abstract class AbstractGeocognitionElement implements
		GeocognitionElement {
	private ArrayList<EditableElementListener> listeners = new ArrayList<EditableElementListener>();
	private GeocognitionElement parent = null;

	protected void fireContentChanged() {
		for (EditableElementListener listener : listeners) {
			listener.contentChanged(this);
		}
	}

	protected void fireSave() {
		for (EditableElementListener listener : listeners) {
			listener.saved(this);
		}
	}

	@Override
	public void addElementListener(EditableElementListener listener) {
		listeners.add(listener);
	}

	@Override
	public boolean removeElementListener(EditableElementListener listener) {
		return listeners.remove(listener);
	}

	protected void fireIdChanged() {
		for (EditableElementListener listener : listeners) {
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
