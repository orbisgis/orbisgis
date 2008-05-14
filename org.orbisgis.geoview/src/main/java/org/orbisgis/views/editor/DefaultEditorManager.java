package org.orbisgis.views.editor;

import org.orbisgis.editor.IEditor;
import org.orbisgis.views.documentCatalog.IDocument;

public class DefaultEditorManager implements EditorManager {

	private EditorPanel editor;

	public DefaultEditorManager(EditorPanel editor) {
		this.editor = editor;
	}

	public IDocument getActiveDocument() {
		return editor.getCurrentDocument();
	}

	public IEditor getActiveEditor() {
		if (editor.getCurrentEditor() == null) {
			return null;
		} else {
			return editor.getCurrentEditor().getEditor();
		}
	}

}
