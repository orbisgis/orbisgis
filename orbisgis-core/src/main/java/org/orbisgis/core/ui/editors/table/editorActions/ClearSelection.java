package org.orbisgis.core.ui.editors.table.editorActions;

import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.core.ui.editors.table.TableEditableElement;

public class ClearSelection implements IEditorAction {

	@Override
	public void actionPerformed(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor
				.getElement();

		element.getSelection().clearSelection();
	}



	@Override
	public boolean isEnabled(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor
				.getElement();

		return element.getSelection().getSelectedRows().length > 0;
	}

	@Override
	public boolean isVisible(IEditor editor) {
		return true;
	}



}
