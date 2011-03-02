package org.orbisgis.core.geocognition;

import java.util.HashMap;
import java.util.Map;

import org.orbisgis.core.edition.AbstractEditableElement;

public abstract class AbstractGeocognitionElement extends
		AbstractEditableElement implements GeocognitionElement {
	private GeocognitionElement parent = null;

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
