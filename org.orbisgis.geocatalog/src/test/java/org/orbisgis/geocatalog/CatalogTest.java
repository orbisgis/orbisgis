package org.orbisgis.geocatalog;

import junit.framework.TestCase;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.ResourceTreeModel;

public class CatalogTest extends TestCase {

	public void testResourcesModifedInDSF() throws Exception {
		Catalog cat = new Catalog();
		OrbisgisCore.getDSF().registerDataSource("source",
				new FileSourceDefinition("a.csv"));
		assertTrue(cat.getTreeModel().getRoot().getChildAt(0).getName().equals(
				"source"));
		OrbisgisCore.getDSF().getSourceManager().rename("source", "source2");
		assertTrue(cat.getTreeModel().getRoot().getChildAt(0).getName().equals(
				"source2"));
		OrbisgisCore.getDSF().remove("source2");
		assertTrue(cat.getTreeModel().getRoot().getChildCount() == 0);
	}

	public void testResourcesModifiedInCatalog() throws Exception {
		Catalog cat = new Catalog();
		ResourceTreeModel model = cat.getTreeModel();
		OrbisgisCore.getDSF().registerDataSource("source",
				new FileSourceDefinition("a.csv"));
		IResource res = model.getRoot().getChildAt(0);
		res.setName("source2");
		assertTrue(OrbisgisCore.getDSF().exists("source2"));
		assertTrue(!OrbisgisCore.getDSF().exists("source"));
		model.removeNode(res);
		// TODO remove when solved
		// assertTrue(!OrbisgisCore.getDSF().exists("source2"));
	}
}
