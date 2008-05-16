package org.orbisgis.views.documentCatalog;

import java.util.ArrayList;

import javax.swing.Icon;

public abstract class AbstractDocument implements IDocument {

	private String name;
	private ArrayList<DocumentListener> listeners = new ArrayList<DocumentListener>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireNameChanged();
	}

	private void fireNameChanged() {
		for (DocumentListener listener : listeners) {
			listener.nameChanged(new DocumentEvent(this));
		}
	}

	protected void fireClosing() {
		for (DocumentListener listener : listeners) {
			listener.documentClosing(new DocumentEvent(this));
		}
	}

	public Icon getIcon() {
		return null;
	}

	public void addDocumentListener(DocumentListener listener) {
		listeners.add(listener);
	}

	public void removeDocumentListener(DocumentListener listener) {
		listeners.remove(listener);
	}
}
