package org.orbisgis.core.ui.configuration.ui;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.orbisgis.core.ui.components.resourceTree.ResourceTree;

/**
 * Resource tree that doesn't allow drag and drop or popup menu
 */
public class BasicResourceTree extends ResourceTree {
	@Override
	protected boolean doDrop(Transferable trans, Object node) {
		return false;
	}

	@Override
	protected Transferable getDragData(DragGestureEvent dge) {
		return null;
	}

	@Override
	public JPopupMenu getPopup() {
		return null;
	}
	
	@Override
	protected boolean isDroppable(TreePath path) {
		return false;
	}
}
