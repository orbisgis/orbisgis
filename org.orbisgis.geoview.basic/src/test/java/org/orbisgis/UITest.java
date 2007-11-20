package org.orbisgis;

import java.awt.datatransfer.Transferable;
import java.io.File;

import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.gdms.data.NoSuchTableException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.EPWindowHelper;
import org.orbisgis.core.FileWizard;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.EPGeocatalogResourceActionHelper;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geoview.EPLayerWizardHelper;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.OGMapControlModel;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.toc.EPTocLayerActionHelper;
import org.orbisgis.geoview.toc.Toc;
import org.orbisgis.pluginManager.Main;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;

public class UITest extends TestCase {

	private static Catalog catalog;

	private static OGMapControlModel mapModel;

	private static GeoView2D geoview;

	private static Toc toc;

	public void testAddFile() throws Exception {

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		// Get catalog reference
		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];
		catalog = geoCatalog.getCatalog();

		// open a vectorial file
		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "vectorial");
		IResource vectorial = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", null)[0];

		// open a tif
		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "tif");
		IResource raster = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", null)[0];

		// Assert it has been opened
		assertTrue(vectorial.getIcon(false) != null);
		assertTrue(raster.getIcon(false) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
	}

	public void testDragToToC() throws Exception {
		// Get geoview and toc instance
		geoview = (GeoView2D) EPWindowHelper
				.getWindows("org.orbisgis.geoview.Window")[0];
		mapModel = geoview.getMapModel();
		toc = (Toc) geoview.getView("org.orbisgis.geoview.Toc");

		// Select both resources
		IResource vectorial = catalog.getTreeModel().getRoot().getResourceAt(0);
		TreePath tp1 = new TreePath(vectorial.getResourcePath());
		IResource raster = catalog.getTreeModel().getRoot().getResourceAt(1);
		TreePath tp2 = new TreePath(raster.getResourcePath());
		catalog.setSelection(new TreePath[] { tp1, tp2 });

		// Drag and drop
		Transferable trans = catalog.getDragData(null);
		toc.doDrop(trans, null);

		// Assert two layers has been added
		ILayer[] layers = mapModel.getLayers().getChildren();
		assertTrue(layers.length == 2);
	}

	public void testDragLayerToFolder() throws Exception {
		// Create a group
		EPTocLayerActionHelper.execute(geoview,
				"org.orbisgis.geoview.toc.CreateGroupAction", new ILayer[0]);
		ILayer group = mapModel.getLayers().getChildren()[2];

		// Select the raster layer
		ILayer layer = mapModel.getLayers().getChildren()[1];
		ILayer[] path = layer.getLayerPath();
		TreePath tp = new TreePath(path);
		toc.setSelection(new TreePath[] { tp });

		// Drag and drop
		Transferable trans = toc.getDragData(null);
		toc.doDrop(trans, group);

		// Assert layer is moved
		assertTrue(layer.getParent() == group);
		assertTrue(group.getParent() == mapModel.getLayers());
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 3);

		// We select again the layer
		path = layer.getLayerPath();
		tp = new TreePath(path);
		toc.setSelection(new TreePath[] { tp });

		// drag and drop outside group
		trans = toc.getDragData(null);
		toc.doDrop(trans, null);

		// Assert group is empty
		assertTrue(layer.getParent() == group.getParent());
		assertTrue(group.getChildren().length == 0);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 3);

		// Remove group
		mapModel.getLayers().remove(group);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 2);
	}

	public void testChangeOrder() throws Exception {
		// Get references to layers
		ILayer vectorial = mapModel.getLayers().getChildren()[0];
		ILayer raster = mapModel.getLayers().getChildren()[1];

		// Select one
		ILayer[] path = vectorial.getLayerPath();
		TreePath tp = new TreePath(path);
		toc.setSelection(new TreePath[] { tp });

		// Drag and drop on top of the other
		Transferable trans = toc.getDragData(null);
		toc.doDrop(trans, raster);

		// Assert the places has been changed
		assertTrue(vectorial == mapModel.getLayers().getChildren()[1]);
		assertTrue(raster == mapModel.getLayers().getChildren()[0]);

		// Remove both layers
		mapModel.getLayers().remove(vectorial);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 1);
		mapModel.getLayers().remove(raster);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 0);
	}

	public void testDeleteFile() throws Exception {
		// We get both resources
		IResource[] res = catalog.getTreeModel().getRoot().getResources();

		// Delete both
		EPGeocatalogResourceActionHelper.executeAction(catalog,
				"org.orbisgis.geocatalog.DeleteResource", res);

		// Assert there is nothing in DataSourceFactory nor in catalog
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);
		assertTrue(catalog.getTreeModel().getRoot().getResources().length == 0);
	}

	public void testCatalogAddFileOnFile() throws Exception {
		// Open a file
		IResource res = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", null)[0];

		// right click on the opened and open another
		IResource res2 = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", res)[0];

		// Assert it has been added to the parent
		assertTrue(res2.getParentResource() == res.getParentResource());
		assertTrue(res.getResources().length == 0);

		// Remove one
		res.getParentResource().removeResource(res);
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 1);

		// Remove the other
		res2.getParentResource().removeResource(res2);
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 0);
	}

	public void testAddLayer() throws Exception {
		// Assert toc is empty
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		// Add a vectorial layer
		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "vectorial");
		EPLayerWizardHelper.runWizard(geoview,
				"org.orbisgis.geoview.NewFileWizard");
		// Add a raster layer
		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "tif");
		EPLayerWizardHelper.runWizard(geoview,
				"org.orbisgis.geoview.NewFileWizard");

		// assert they have been added
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
		ILayer[] layers = mapModel.getLayers().getChildren();
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[0].getName()) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[1].getName()) != null);
	}

	public void testRename() throws Exception {
		// get the raster layer and it's name
		ILayer layer = mapModel.getLayers().getChildren()[1];
		String mainName = layer.getName();

		// Change the name
		String alias = "newName";
		layer.setName(alias);

		// Assert the alias has been added
		String mainNameDSF = OrbisgisCore.getDSF().getSourceManager()
				.getMainNameFor(alias);
		assertTrue(mainName.equals(mainNameDSF));
	}

	public void testDeleteLayer() throws Exception {
		// Iterate over layers and remove everything
		ILayer root = mapModel.getLayers();
		ILayer[] layers = root.getChildren();
		for (ILayer layer : layers) {
			SourceManager sourceManager = OrbisgisCore.getDSF()
					.getSourceManager();
			String alias = layer.getName();
			boolean isMainName = sourceManager.getMainNameFor(alias).equals(
					alias);
			root.remove(layer);

			// Assert the alias has been removed from data source factory
			if (!isMainName) {
				try {
					sourceManager.getMainNameFor(alias);
					assertTrue(false);
				} catch (NoSuchTableException e) {
				}
			}
		}
	}

	static {
		try {
			Main.main(new String[] { "src/test/resources/plugin-list.xml" });
			UIFactory
					.setPersistencyDirectory(new File("src/test/resources/sif"));
			PluginManager.setDebug(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
