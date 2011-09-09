package org.orbisgis.core.edition;

import java.util.ArrayList;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;

public abstract class AbstractEditableElement implements EditableElement {

	private ArrayList<EditableElementListener> listeners = new ArrayList<EditableElementListener>();

	protected void fireContentChanged() {
		for (EditableElementListener listener : listeners) {
			listener.contentChanged(this);			
		}
		WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
		wbContext.setLastAction("Toc changes");
	}

	protected void fireSave() {
		for (EditableElementListener listener : listeners) {
			listener.saved(this);			
		}
		WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
		wbContext.setLastAction("Toc changes");
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
