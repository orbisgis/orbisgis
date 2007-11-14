package org.orbisgis;

import java.io.File;

import junit.framework.TestCase;

import org.orbisgis.core.EPWindowHelper;
import org.orbisgis.core.FileWizard;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.NodeFilter;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.EPGeocatalogResourceActionHelper;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geoview.EPLayerWizardHelper;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.OGMapControlModel;
import org.orbisgis.pluginManager.Main;
import org.sif.UIFactory;

public class UITest extends TestCase {

	private static Catalog catalog;

	private static OGMapControlModel mapModel;

	public void testAddFile() throws Exception {

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "add");

		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];
		catalog = geoCatalog.getCatalog();

		IResource[] resource = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", null);

		assertTrue(resource[0].getIcon(false) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
	}

	public void testDeleteFile() throws Exception {
		IResource[] res = catalog.getTreeModel().getNodes(new NodeFilter() {

			public boolean accept(IResource resource) {
				if (resource instanceof AbstractGdmsSource) {
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

	public void testAddFileOnFile() throws Exception {
		IResource[] res = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", null);
		IResource[] res2 = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", res[0]);
		assertTrue(res2[0].getParent() == res[0].getParent());
		catalog.getTreeModel().removeNode(res[0]);
		catalog.getTreeModel().removeNode(res2[0]);
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 0);
	}

	public void testAddLayer() throws Exception {
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "add");

		GeoView2D geoview = (GeoView2D) EPWindowHelper
				.getWindows("org.orbisgis.geoview.Window")[0];
		mapModel = geoview.getMapModel();

		EPLayerWizardHelper.runWizard(geoview,
				"org.orbisgis.geoview.NewFileWizard");

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
		String layerName = mapModel.getLayers().getChildren()[0].getName();
		assertTrue(OrbisgisCore.getDSF().getSourceManager()
				.getSource(layerName) != null);
	}

	static {
		try {
			Main.main(new String[] { "src/test/resources/plugin-list.xml" });
			UIFactory
					.setPersistencyDirectory(new File("src/test/resources/sif"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
