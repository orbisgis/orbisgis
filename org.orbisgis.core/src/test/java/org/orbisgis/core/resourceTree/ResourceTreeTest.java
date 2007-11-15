package org.orbisgis.core.resourceTree;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;


import junit.framework.TestCase;

public class ResourceTreeTest extends TestCase {

	public void testNotCollapseWhenAddingAResource() throws Exception {
		ResourceTree cat = new ResourceTree() {

			@Override
			public JPopupMenu getPopup() {
				return null;
			}

			@Override
			public void drop(DropTargetDropEvent dtde) {

			}

			public void dragGestureRecognized(DragGestureEvent dge) {

			}

		};
		ResourceTreeModel model = new ResourceTreeModel(cat);
		cat.setModel(model);
		IResource root = model.getRoot();
		IResource folder = ResourceFactory.createResource("Another folder",
				new Folder());
		root.addResource(folder);
		IResource folder2 = ResourceFactory.createResource("third folder",
				new Folder());
		folder.addResource(folder2);

		TreePath tp = new TreePath(folder.getResourcePath());
		cat.tree.expandPath(tp);
		assertTrue(cat.tree.getExpandedDescendants(tp) != null);
		folder.addResource((ResourceFactory.createResource("will it collapse?",
				new Folder())));
		assertTrue(cat.tree.getExpandedDescendants(tp) != null);
	}

}
