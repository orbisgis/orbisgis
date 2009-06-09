package org.orbisgis.core.ui.components.text;

import java.util.ArrayList;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;


/**
 * Class with undo edit grouping support
 * 
 * @author victorzinho
 * 
 */
public class UndoableDocument extends DefaultStyledDocument {
	protected boolean isGrouping;
	private CompoundEdit compound;
	private UndoManager undoManager;
	private ArrayList<UndoRedoListener> listeners;

	/**
	 * Creates a new undoable document
	 */
	public UndoableDocument() {
		isGrouping = false;
		compound = new CompoundEdit();
		undoManager = new UndoManager();
		listeners = new ArrayList<UndoRedoListener>();
		addUndoableEditListener(undoManager);
	}

	@Override
	protected void fireChangedUpdate(DocumentEvent e) {
		if (isGrouping) {
			compound.addEdit((UndoableEdit) e);
		}

		super.fireChangedUpdate(e);
	}

	@Override
	protected void fireInsertUpdate(DocumentEvent e) {
		if (isGrouping) {
			compound.addEdit((UndoableEdit) e);
		}

		super.fireInsertUpdate(e);
	}

	@Override
	protected void fireRemoveUpdate(DocumentEvent e) {
		if (isGrouping) {
			compound.addEdit((UndoableEdit) e);
		}

		super.fireRemoveUpdate(e);
	}

	@Override
	protected void fireUndoableEditUpdate(UndoableEditEvent e) {
		if (!isGrouping) {
			super.fireUndoableEditUpdate(e);
		}
	}

	/**
	 * Sets the grouping state of this editor to true / false. This method it's
	 * used to group the undo edits between <code>groupUndoEdits(true)</code>
	 * and <code>groupUndoEdits(false)</code>. When this method is called with
	 * <code>true</code> flag, no <code>UndoableEditEvent</code> is fired. When
	 * this method it's called with <code>false</code> flag, it calls
	 * <code>fireUndoableEditUpdate</code> with the grouped edits.
	 * 
	 * @param b
	 *            flag to enable / disable
	 */
	public void groupUndoEdits(boolean b) {
		if (!isGrouping && b) {
			isGrouping = b;
			compound = new CompoundEdit();
		} else if (isGrouping && !b) {
			isGrouping = b;
			compound.end();
			fireUndoableEditUpdate(new UndoableEditEvent(this, compound));
		}
	}

	/**
	 * Discards all undo edits performed
	 */
	public void resetUndoEdits() {
		undoManager.discardAllEdits();
	}

	/**
	 * Performs an undo action on the UndoManager and calls all the given
	 * listeners
	 * 
	 * @param undoManager
	 *            manager performing undo
	 * @param listeners
	 *            listeners to call
	 */
	public void undo() {
		if (listeners != null) {
			for (UndoRedoListener listener : listeners) {
				listener.preUndo();
			}
		}

		if (undoManager != null) {
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		}

		if (listeners != null) {
			for (UndoRedoListener listener : listeners) {
				listener.undoPerformed();
			}
		}
	}

	/**
	 * Performs an redo action on the UndoManager and calls all the given
	 * listeners
	 * 
	 * @param undoManager
	 *            manager performing redo
	 * @param listeners
	 *            listeners to call
	 */
	public void redo() {
		if (listeners != null) {
			for (UndoRedoListener listener : listeners) {
				listener.preRedo();
			}
		}

		if (undoManager != null) {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}

		if (listeners != null) {
			for (UndoRedoListener listener : listeners) {
				listener.redoPerformed();
			}
		}
	}

	public boolean canUndo() {
		return undoManager.canUndo();
	}

	public boolean canRedo() {
		return undoManager.canRedo();
	}

	public void addUndoRedoListener(UndoRedoListener l) {
		listeners.add(l);
	}

	public boolean removeUndoRedoListener(UndoRedoListener l) {
		return listeners.remove(l);
	}
}
