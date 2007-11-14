package org.orbisgis.geocatalog;

import junit.framework.TestCase;

import org.orbisgis.core.resourceTree.Folder;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.resources.FileResource;

public class ResourceTest extends TestCase {

	public void testAddBeforeFolders() throws Exception {
		Folder root = new Folder("root");
		Folder f1 = new Folder("f1");
		Folder f2 = new Folder("f2");
		Folder f3 = new Folder("f3");
		root.addChild(f1);
		root.addChild(f2);
		root.addChild(f3);

		FileResource gs = new FileResource("source", "/tmp");
		root.addChild(gs, 0);
		assertTrue(foldersFirst(root));
		root.addChild(gs, 1);
		assertTrue(foldersFirst(root));
		root.addChild(gs, 2);
		assertTrue(foldersFirst(root));

		Folder last = new Folder("last");
		root.addChild(last);

		assertTrue(foldersFirst(root));
	}

	public void testInsertLast() throws Exception {
		Folder root = new Folder("root");
		Folder f1 = new Folder("f1");
		Folder f2 = new Folder("f2");
		root.addChild(f1);
		root.addChild(f2);
		assertTrue(root.getChildAt(0) == f1);
		assertTrue(root.getChildAt(1) == f2);
	}

	private boolean foldersFirst(IResource node) {
		IResource[] children = node.getChildren();
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
