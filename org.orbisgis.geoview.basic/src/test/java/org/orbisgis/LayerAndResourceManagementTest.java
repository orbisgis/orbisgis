package org.orbisgis;

import java.awt.datatransfer.Transferable;

import javax.swing.tree.TreePath;

import org.gdms.data.NoSuchTableException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.EPGeocatalogResourceActionHelper;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.toc.EPTocLayerActionHelper;

public class LayerAndResourceManagementTest extends UITest {

	public void testAddFile() throws Exception {

		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		// open a vectorial file
		IResource vectorial = openFile("vectorial");

		// open a tif
		IResource raster = openFile("tif");

		// Assert it has been opened
		assertTrue(vectorial.getIcon(false) != null);
		assertTrue(raster.getIcon(false) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
	}

	public void testDragToToC() throws Exception {

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
		ILayer[] layers = viewContext.getRootLayer().getChildren();
		assertTrue(layers.length == 2);
	}

	public void testDragLayerToFolder() throws Exception {
		// Create a group
		EPTocLayerActionHelper.execute(geoview,
				"org.orbisgis.geoview.toc.CreateGroupAction", new ILayer[0]);
		ILayer group = viewContext.getRootLayer().getChildren()[2];

		// Select the raster layer
		ILayer layer = viewContext.getRootLayer().getChildren()[1];
		viewContext.setSelectedLayers(new ILayer[] { layer });

		// Drag and drop
		Transferable trans = toc.getDragData(null);
		toc.doDrop(trans, group);

		// Assert layer is moved
		assertTrue(layer.getParent() == group);
		assertTrue(group.getParent() == viewContext.getRootLayer());
		assertTrue(viewContext.getRootLayer().getLayersRecursively().length == 3);

		// We select again the layer
		viewContext.setSelectedLayers(new ILayer[] { layer });

		// drag and drop outside group
		trans = toc.getDragData(null);
		toc.doDrop(trans, null);

		// Assert group is empty
		assertTrue(layer.getParent() == group.getParent());
		assertTrue(group.getChildren().length == 0);
		assertTrue(viewContext.getRootLayer().getLayersRecursively().length == 3);

		// Remove group
		viewContext.getRootLayer().remove(group);
		assertTrue(viewContext.getRootLayer().getLayersRecursively().length == 2);
	}

	public void testChangeOrder() throws Exception {
		// Get references to layers
		ILayer vectorial = viewContext.getRootLayer().getChildren()[0];
		ILayer raster = viewContext.getRootLayer().getChildren()[1];

		// Select one
		viewContext.setSelectedLayers(new ILayer[] { vectorial });

		// Drag and drop on top of the other
		Transferable trans = toc.getDragData(null);
		toc.doDrop(trans, raster);

		// Assert the places has been changed
		assertTrue(vectorial == viewContext.getRootLayer().getChildren()[1]);
		assertTrue(raster == viewContext.getRootLayer().getChildren()[0]);

		// Remove both layers
		viewContext.getRootLayer().remove(vectorial);
		assertTrue(viewContext.getRootLayer().getLayersRecursively().length == 1);
		viewContext.getRootLayer().remove(raster);
		assertTrue(viewContext.getRootLayer().getLayersRecursively().length == 0);
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
		addLayer("vectorial");
		// Add a raster layer
		addLayer("tif");

		// assert they have been added
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
		ILayer[] layers = viewContext.getRootLayer().getChildren();
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[0].getName()) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[1].getName()) != null);
	}

	public void testRename() throws Exception {
		// get the raster layer and it's name
		ILayer layer = viewContext.getRootLayer().getChildren()[1];
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
		ILayer root = viewContext.getRootLayer();
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

}
