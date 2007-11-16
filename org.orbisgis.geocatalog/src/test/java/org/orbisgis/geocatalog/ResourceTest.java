package org.orbisgis.geocatalog;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.geocatalog.resources.FileResource;
import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceFactory;
import org.orbisgis.geocatalog.resources.ResourceTreeModel;

public class ResourceTest extends TestCase {

	private ResourceTreeModel model = new ResourceTreeModel(new JTree());

	public void testAddBeforeFolders() throws Exception {
		IResource root = ResourceFactory.createResource("root", new Folder(),
				model);
		IResource f1 = ResourceFactory.createResource("f1", new Folder());
		IResource f2 = ResourceFactory.createResource("f2", new Folder());
		IResource f3 = ResourceFactory.createResource("f3", new Folder());
		root.addResource(f1);
		root.addResource(f2);
		root.addResource(f3);

		IResource gs = ResourceFactory.createResource("source",
				new FileResource());
		;
		root.addResource(gs);
		assertTrue(foldersFirst(root));
		root.addResource(gs);
		assertTrue(foldersFirst(root));
		root.addResource(gs);
		assertTrue(foldersFirst(root));

		IResource last = ResourceFactory.createResource("last", new Folder());
		root.addResource(last);

		assertTrue(foldersFirst(root));
	}

	public void testInsertLast() throws Exception {
		IResource root = ResourceFactory.createResource("root", new Folder(),
				model);
		IResource f1 = ResourceFactory.createResource("f1", new Folder());
		IResource f2 = ResourceFactory.createResource("f2", new Folder());
		root.addResource(f1);
		root.addResource(f2);
		assertTrue(root.getResourceAt(0) == f1);
		assertTrue(root.getResourceAt(1) == f2);
	}

	private boolean foldersFirst(IResource node) {
		IResource[] children = node.getResources();
		int firstNoFolder = -1;
		for (int i = 0; i < children.length; i++) {
			if (!(children[i] instanceof Folder)) {
				firstNoFolder = i;
				break;
			}
		}

		for (int i = firstNoFolder; i < children.length; i++) {
			if (children[i] instanceof Folder) {
				return false;
			}
		}

		return true;
	}

	public void testNotCollapseWhenAddingAResource() throws Exception {
		ResourceTree cat = new ResourceTree() {

			@Override
			public JPopupMenu getPopup() {
				return null;
			}

			@Override
			protected boolean doDrop(Transferable trans, Object node) {
				return false;
			}

			@Override
			protected Transferable getDragData(DragGestureEvent dge) {
				return null;
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
		cat.getTree().expandPath(tp);
		assertTrue(cat.getTree().getExpandedDescendants(tp) != null);
		folder.addResource((ResourceFactory.createResource("will it collapse?",
				new Folder())));
		assertTrue(cat.getTree().getExpandedDescendants(tp) != null);
	}

}
