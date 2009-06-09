package org.orbisgis.core.ui.editors.table.editorActions;

import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.TableEditor;

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
