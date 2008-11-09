package org.orbisgis.editors.table.editorActions;

import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.TableEditor;

public class SelectionUp implements IEditorAction {

	@Override
	public void actionPerformed(IEditor editor) {
		TableEditor te = (TableEditor) editor;
		te.moveSelectionUp();
	}

	@Override
	public boolean isEnabled(IEditor editor) {
		return ((TableEditableElement) editor.getElement()).getSelection()
				.getSelectedRows().length > 0;
	}

	@Override
	public boolean isVisible(IEditor editor) {
		return true;
	}

}
