package org.orbisgis.geocatalog;

import javax.swing.tree.TreePath;

import org.orbisgis.geocatalog.resources.Folder;

import junit.framework.TestCase;

public class CatalogTest extends TestCase {

	public void testNotCollapseWhenAddingAResource() throws Exception {
		Catalog cat = new Catalog();
		CatalogModel model = cat.getCatalogModel();
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
