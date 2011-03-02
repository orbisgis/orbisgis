package org.orbisgis.core.ui.components.text;

public interface UndoRedoListener {
	/**
	 * Called when an undo action is going to be performed
	 */
	public void preUndo();

	/**
	 * Called when a redo action is going to be performed
	 */
	public void preRedo();

	/**
	 * Called after an undo action has been performed
	 */
	public void undoPerformed();

	/**
	 * Called after a redo action has been performed
	 */
	public void redoPerformed();
}
