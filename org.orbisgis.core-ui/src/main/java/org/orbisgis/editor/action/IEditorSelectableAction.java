package org.orbisgis.editor.action;

import org.orbisgis.editor.IEditor;

public interface IEditorSelectableAction extends IEditorAction {

	/**
	 * Return true if the action should appear as 'selected'
	 *
	 * @param editor
	 *            The active editor. Its id is equal to the id specified in the
	 *            plugin.xml for this action
	 * @return
	 */
	boolean isSelected(IEditor editor);

}
