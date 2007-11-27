package org.orbisgis;

import java.io.File;

import junit.framework.TestCase;

import org.orbisgis.core.EPWindowHelper;
import org.orbisgis.core.FileWizard;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.EPGeocatalogResourceActionHelper;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geoview.EPLayerWizardHelper;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.geoview.table.Table;
import org.orbisgis.geoview.toc.Toc;
import org.orbisgis.pluginManager.Main;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ViewContext;
import org.sif.UIFactory;

/**
 * Base class to test user interface. As some components in the system are
 * static, these tests are sequential and its behavior is based in the status
 * given by the execution of the previous test, so they cannot be run
 * individually
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class UITest extends TestCase {

	protected static Catalog catalog;

	protected static ViewContext viewContext;

	protected static GeoView2D geoview;

	protected static Toc toc;

	protected static Table table;

	protected static SQLConsolePanel sqlConsole;

	static {
		try {
			Main.main(new String[] { "src/test/resources/plugin-list.xml" });
			UIFactory
					.setPersistencyDirectory(new File("src/test/resources/sif"));
			PluginManager.setTesting(true);

			// Get catalog reference
			GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
					.getWindows("org.orbisgis.geocatalog.Window")[0];
			catalog = geoCatalog.getCatalog();

			// Get geoview and toc instance
			geoview = (GeoView2D) EPWindowHelper
					.getWindows("org.orbisgis.geoview.Window")[0];
			geoview.showView("org.orbisgis.geoview.Toc");
			geoview.showView("org.orbisgis.geoview.SQLConsole");
			geoview.showView("org.orbisgis.geoview.Table");
			viewContext = geoview.getViewContext();
			toc = (Toc) geoview.getView("org.orbisgis.geoview.Toc");
			sqlConsole = (SQLConsolePanel) geoview
					.getView("org.orbisgis.geoview.SQLConsole");
			table = (Table) geoview.getView("org.orbisgis.geoview.Table");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void clearCatalog() {
		EPGeocatalogResourceActionHelper.executeAction(catalog,
				"org.orbisgis.geocatalog.ClearCatalog", new IResource[0]);
	}

	protected IResource openFile(String sifInput) {
		return openFile(sifInput, null);
	}

	protected IResource openFile(String sifInput, IResource parent) {
		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, sifInput);
		IResource vectorial = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", parent)[0];
		return vectorial;
	}

	protected ILayer addLayer(String sifInput) {
		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, sifInput);
		return EPLayerWizardHelper.runWizard(geoview,
				"org.orbisgis.geoview.NewFileWizard")[0];
	}

}
