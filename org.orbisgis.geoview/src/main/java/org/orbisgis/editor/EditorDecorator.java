package org.orbisgis.editor;

import java.awt.Component;

import javax.swing.Icon;

import org.orbisgis.PersistenceException;
import org.orbisgis.views.documentCatalog.DocumentDecorator;
import org.orbisgis.views.documentCatalog.IDocument;

public class EditorDecorator implements IEditor {

	private IEditor editor;
	private Icon icon;
	private String id;

	public EditorDecorator(IEditor editor, Icon icon, String id) {
		this.editor = editor;
		this.icon = icon;
		this.id = id;
	}

	public boolean acceptDocument(IDocument doc) {
		return editor.acceptDocument(doc);
	}

	public void delete() {
		editor.delete();
	}

	public Component getComponent() {
		return editor.getComponent();
	}

	public void initialize() {
		editor.initialize();
	}

	public void loadStatus() throws PersistenceException {
		editor.loadStatus();
	}

	public void saveStatus() throws PersistenceException {
		editor.saveStatus();
	}

	public void setDocument(IDocument doc) {
		if (doc instanceof DocumentDecorator) {
			doc = ((DocumentDecorator) doc).getDocument();
		}
		editor.setDocument(doc);
	}

	public String getTitle() {
		return editor.getTitle();
	}

	public Icon getIcon() {
		return icon;
	}

	public IDocument getDocument() {
		return editor.getDocument();
	}

	public IEditor getEditor() {
		return editor;
	}

	public String getId() {
		return id;
	}

}
