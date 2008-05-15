package org.orbisgis.editor;


/**
 * Listener for edition events
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface EditorListener {

	/**
	 * Notifies the active editor has changed
	 *
	 * @param previous
	 *            the previously active editor. Null if there was no active
	 *            editor
	 * @param current
	 *            The currently active editor. Null if no editor is selected
	 */
	void activeEditorChanged(IEditor previous, IEditor current);

	/**
	 * Notifies an editor has been closed
	 *
	 * @param editor
	 */
	void activeEditorClosed(IEditor editor);

}
