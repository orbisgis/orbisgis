package org.orbisgis.views.editor;

import org.orbisgis.editor.IEditor;
import org.orbisgis.views.documentCatalog.IDocument;

public interface EditorManager {

	/**
	 * Gets the active document
	 *
	 * @return
	 */
	IDocument getActiveDocument();

	/**
	 * Gets the active editor
	 *
	 * @return
	 */
	IEditor getActiveEditor();

}
