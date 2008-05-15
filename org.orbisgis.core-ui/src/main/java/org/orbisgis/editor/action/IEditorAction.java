package org.orbisgis.editor.action;

import org.orbisgis.editor.IEditor;

/**
 *
 * Editor action manager
 *
 * @author Fernando Gonzalez Cortes
 */
public interface IEditorAction {

	/**
	 * Executes the action
	 *
	 * @param editor
	 *            Active editor. Its id is equal to the id specified in the
	 *            plugin.xml for this action
	 */
	public void actionPerformed(IEditor editor);

	/**
	 * Returns true if the tool should be enabled
	 *
	 * @param editor
	 *            Active editor. Its id is equal to the id specified in the
	 *            plugin.xml for this action
	 * @return
	 */
	public boolean isEnabled(IEditor editor);

	/**
	 * Returns true if the action should be visible. If the active editor is not
	 * the one associated to this action, this method is not even invoked and
	 * the action is not visible.
	 *
	 * @param editor
	 *            Active editor. Its id is equal to the id specified in the
	 *            plugin.xml for this action
	 * @return
	 */
	public boolean isVisible(IEditor editor);

}
