package org.orbisgis.core.resourceTree;

import javax.swing.tree.TreePath;

import junit.framework.TestCase;

public class ResourceTreeTest extends TestCase {

	public void testNotCollapseWhenAddingAResource() throws Exception {
		ResourceTree cat = new ResourceTree();
		ResourceTreeModel model = cat.getCatalogModel();
		Folder folder = new Folder("Another folder");
		folder.addChild(new Folder("third folder"));
		model.insertNode(folder);

		TreePath tp = new TreePath(folder.getPath());
		cat.tree.expandPath(tp);
		assertTrue(cat.tree.getExpandedDescendants(tp) != null);
		model.insertNode(new Folder("will it collapse?"));
		assertTrue(cat.tree.getExpandedDescendants(tp) != null);
	}

}
