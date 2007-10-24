package org.orbisgis.geocatalog;

import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.geocatalog.resources.IResource;

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

	public void testResourcesModifedInDSF() throws Exception {
		Catalog cat = new Catalog();
		OrbisgisCore.getDSF().registerDataSource("source",
				new FileSourceDefinition("a.csv"));
		assertTrue(cat.getCatalogModel().getRoot().getChildAt(0).getName()
				.equals("source"));
		OrbisgisCore.getDSF().rename("source", "source2");
		assertTrue(cat.getCatalogModel().getRoot().getChildAt(0).getName()
				.equals("source2"));
		OrbisgisCore.getDSF().remove("source2");
		assertTrue(cat.getCatalogModel().getRoot().getChildCount() == 0);
	}

	public void testResourcesModifiedInCatalog() throws Exception {
		Catalog cat = new Catalog();
		CatalogModel model = cat.getCatalogModel();
		OrbisgisCore.getDSF().registerDataSource("source",
				new FileSourceDefinition("a.csv"));
		IResource res = model.getRoot().getChildAt(0);
		res.setName("source2");
		assertTrue(OrbisgisCore.getDSF().existDS("source2"));
		assertTrue(!OrbisgisCore.getDSF().existDS("source"));
	}
}
