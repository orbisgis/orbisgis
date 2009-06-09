package org.orbisgis.core.edition;

import java.util.ArrayList;

public abstract class AbstractEditableElement implements EditableElement {
	
	private ArrayList<EditableElementListener> listeners = new ArrayList<EditableElementListener>();

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

}
