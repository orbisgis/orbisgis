package org.orbisgis.geocatalog;

import javax.swing.JTree;

import junit.framework.TestCase;

import org.orbisgis.core.resourceTree.Folder;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.ResourceFactory;
import org.orbisgis.core.resourceTree.ResourceTreeModel;
import org.orbisgis.geocatalog.resources.FileResource;

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

}
