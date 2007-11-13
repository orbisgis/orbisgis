package org.orbisgis.geocatalog;

import java.io.File;

import junit.framework.TestCase;

import org.orbisgis.core.EPWindowHelper;
import org.orbisgis.core.FileWizard;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.NodeFilter;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.GdmsSource;
import org.orbisgis.pluginManager.Main;
import org.sif.UIFactory;

public class UITest extends TestCase {

	public void testFile() throws Exception {

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "add");

		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];

		EPResourceWizardHelper.runWizard(geoCatalog.getCatalog(),
				"org.orbisgis.geocatalog.NewFileResourceWizard", null);

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
	}

	public void testDeleteFile() throws Exception {
		testFile();

		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];

		Catalog catalog = geoCatalog.getCatalog();
		IResource[] res = catalog.getTreeModel().getNodes(new NodeFilter() {

			public boolean accept(IResource resource) {
				if (resource instanceof GdmsSource) {
					return true;
				} else {
					return false;
				}
			}

		});

		EPGeocatalogResourceActionHelper.executeAction(catalog,
				"org.orbisgis.geocatalog.DeleteResource", res);

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);
	}

	@Override
	protected void setUp() throws Exception {
		Main.main(new String[] { "src/test/resources/plugin-list.xml" });
		OrbisgisCore.getDSF().getSourceManager().removeAll();
		UIFactory.setPersistencyDirectory(new File("src/test/resources/sif"));
	}
}
