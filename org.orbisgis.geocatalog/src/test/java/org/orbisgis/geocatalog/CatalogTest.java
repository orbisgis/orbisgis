package org.orbisgis.geocatalog;

import junit.framework.TestCase;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceTreeModel;

public class CatalogTest extends TestCase {

	public void testResourcesModifedInDSF() throws Exception {
		Catalog cat = new Catalog();
		OrbisgisCore.getDSF().registerDataSource("source",
				new FileSourceDefinition("a.csv"));
		assertTrue(cat.getTreeModel().getRoot().getResourceAt(0).getName().equals(
				"source"));
		OrbisgisCore.getDSF().getSourceManager().rename("source", "source2");
		assertTrue(cat.getTreeModel().getRoot().getResourceAt(0).getName().equals(
				"source2"));
		OrbisgisCore.getDSF().remove("source2");
		assertTrue(cat.getTreeModel().getRoot().getChildCount() == 0);
	}

	public void testResourcesModifiedInCatalog() throws Exception {
		Catalog cat = new Catalog();
		ResourceTreeModel model = cat.getTreeModel();
		OrbisgisCore.getDSF().registerDataSource("source",
				new FileSourceDefinition("a.csv"));
		IResource res = model.getRoot().getResourceAt(0);
		res.setResourceName("source2");
		assertTrue(OrbisgisCore.getDSF().exists("source2"));
		assertTrue(!OrbisgisCore.getDSF().exists("source"));
		res.getParentResource().removeResource(res);
		assertTrue(!OrbisgisCore.getDSF().exists("source2"));
	}
}
