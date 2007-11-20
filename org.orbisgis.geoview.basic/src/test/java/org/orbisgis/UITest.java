package org.orbisgis;

import java.awt.datatransfer.Transferable;
import java.io.File;

import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.gdms.data.NoSuchTableException;
import org.orbisgis.core.EPWindowHelper;
import org.orbisgis.core.FileWizard;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.EPGeocatalogResourceActionHelper;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.NodeFilter;
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

		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "add");

		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];
		catalog = geoCatalog.getCatalog();

		IResource[] resource = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", null);

		assertTrue(resource[0].getIcon(false) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
	}

	public void testDragToToC() throws Exception {
		IResource res = catalog.getTreeModel().getRoot().getResourceAt(0);
		TreePath tp = new TreePath(res.getResourcePath());
		catalog.setSelection(new TreePath[] { tp });
		Transferable trans = catalog.getDragData(null);
		geoview = (GeoView2D) EPWindowHelper
				.getWindows("org.orbisgis.geoview.Window")[0];
		mapModel = geoview.getMapModel();
		toc = (Toc) geoview.getView("org.orbisgis.geoview.Toc");
		toc.doDrop(trans, null);
		ILayer[] layers = mapModel.getLayers().getChildren();
		assertTrue(layers.length == 1);
	}

	public void testDragLayerToFolder() throws Exception {
		ILayer layer = mapModel.getLayers().getChildren()[0];
		EPTocLayerActionHelper.execute(geoview,
				"org.orbisgis.geoview.toc.CreateGroupAction", new ILayer[0]);
		ILayer group = mapModel.getLayers().getChildren()[1];
		ILayer[] path = layer.getLayerPath();
		TreePath tp = new TreePath(path);
		toc.setSelection(new TreePath[] { tp });
		Transferable trans = toc.getDragData(null);
		toc.doDrop(trans, group);
		assertTrue(layer.getParent() == group);
		assertTrue(group.getParent() == mapModel.getLayers());
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 2);

		path = layer.getLayerPath();
		tp = new TreePath(path);
		toc.setSelection(new TreePath[] { tp });
		trans = toc.getDragData(null);
		toc.doDrop(trans, null);
		assertTrue(layer.getParent() == group.getParent());
		assertTrue(group.getChildren().length == 0);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 2);

		mapModel.getLayers().remove(group);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 1);
	}

	public void testChangeOrder() throws Exception {
		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "add");

		EPLayerWizardHelper.runWizard(geoview,
				"org.orbisgis.geoview.NewFileWizard");

		ILayer layer1 = mapModel.getLayers().getChildren()[0];
		ILayer layer2 = mapModel.getLayers().getChildren()[1];
		ILayer[] path = layer1.getLayerPath();
		TreePath tp = new TreePath(path);
		toc.setSelection(new TreePath[] { tp });
		Transferable trans = toc.getDragData(null);
		toc.doDrop(trans, layer2);

		assertTrue(layer1 == mapModel.getLayers().getChildren()[1]);
		assertTrue(layer2 == mapModel.getLayers().getChildren()[0]);

		mapModel.getLayers().remove(layer1);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 1);
		mapModel.getLayers().remove(layer2);
		assertTrue(mapModel.getLayers().getLayersRecursively().length == 0);
	}

	public void testDeleteFile() throws Exception {
		IResource[] res = catalog.getTreeModel().getNodes(new NodeFilter() {

			public boolean accept(IResource resource) {
				if (resource.getResourceType() instanceof AbstractGdmsSource) {
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

	public void testCatalogAddFileOnFile() throws Exception {
		IResource[] res = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", null);
		IResource[] res2 = EPResourceWizardHelper.runWizard(catalog,
				"org.orbisgis.geocatalog.NewFileResourceWizard", res[0]);
		assertTrue(res2[0].getParentResource() == res[0].getParentResource());
		res[0].getParentResource().removeResource(res[0]);
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 1);
		res2[0].getParentResource().removeResource(res2[0]);
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 0);
	}

	public void testAddLayer() throws Exception {
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		UIFactory.setInputFor(FileWizard.FILE_CHOOSER_SIF_ID, "add");

		EPLayerWizardHelper.runWizard(geoview,
				"org.orbisgis.geoview.NewFileWizard");

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
		String layerName = mapModel.getLayers().getChildren()[0].getName();
		assertTrue(OrbisgisCore.getDSF().getSourceManager()
				.getSource(layerName) != null);
	}

	public void testRename() throws Exception {
		ILayer layer = mapModel.getLayers().getChildren()[0];
		String alias = "newName";
		layer.setName(alias);
		String mainName = OrbisgisCore.getDSF().getSourceManager()
				.getMainNameFor(alias);
		assertTrue(mainName != null);
	}

	public void testDeleteLayer() throws Exception {
		ILayer layer = mapModel.getLayers().getChildren()[0];
		String alias = layer.getName();
		mapModel.getLayers().remove(layer);
		try {
			OrbisgisCore.getDSF().getSourceManager().getMainNameFor(alias);
			assertTrue(false);
		} catch (NoSuchTableException e) {
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
