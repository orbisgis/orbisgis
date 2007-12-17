package org.orbisgis.geoview.ui;

import java.awt.datatransfer.Transferable;

import javax.swing.tree.TreePath;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geoview.layerModel.ILayer;

public class LayerAndResourceManagementTest2 extends UITest {

	public void testCatalogAddFileOnFile() throws Exception {
		// Open a file
		IResource res = openFile("hedgerow");

		// right click on the opened and open another
		IResource res2 = openFile("tif", res);

		// Assert it has been added to the parent
		assertTrue(res2.getParentResource() == res.getParentResource());
		assertTrue(res.getResources().length == 0);

		// Remove one
		res.getParentResource().removeResource(res);
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 1);

		// Remove the other
		clearCatalog();
		assertTrue(catalog.getTreeModel().getRoot().getChildCount() == 0);
	}

	public void testRemoveResourceRemovesLayer() throws Exception {
		// Add one layer
		ILayer layer = addLayer("vectorial");

		// Add the same layer
		addLayer("vectorial");

		// Assert only one resource in catalog
		IResource root = catalog.getTreeModel().getRoot();
		assertTrue(catalog.getTreeModel().getChildCount(root) == 1);

		// Change layer name
		layer.setName("a" + System.currentTimeMillis());

		// Clear catalog
		clearCatalog();

		// Assert layers have been removed
		assertTrue(viewContext.getRootLayer().getLayerCount() == 0);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty());
	}

	public void testClearCatalogWithSourceInFolder() throws Exception {
		// Create folder
		IResource folder = createFolder("folder");

		// Open a file
		openFile("hedgerow", folder);

		// Clear catalog
		clearCatalog();

		// Assert layers have been removed
		assertTrue(viewContext.getRootLayer().getLayerCount() == 0);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty());

	}

	public void testChangeWorkspaceToNewOneAndDSFIsEmpty() throws Exception {
		// Open a file
		openFile("hedgerow");

		// Change Workspace
		setWorkspace("empty_workspace");

		// assert dsf is empty
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty());

		// Change Workspace
		setWorkspace("test_workspace");

		// Clear catalog
		clearCatalog();

	}

	public void testDnDTwiceAndSave() throws Exception {

		// Open a resource
		IResource res = openFile("hedgerow");

		// Select it
		TreePath tp = new TreePath(res.getResourcePath());
		catalog.setSelection(new TreePath[] { tp });

		// Drag and drop twice
		Transferable trans = catalog.getDragData(null);
		toc.doDrop(trans, null);
		toc.doDrop(trans, null);

		// Assert two layers has been added
		ILayer[] layers = viewContext.getRootLayer().getChildren();
		assertTrue(layers.length == 2);

		saveAndLoad();

		// Assert we still have two layers
		layers = viewContext.getRootLayer().getChildren();
		assertTrue(layers.length == 2);

		// clean
		clearCatalog();
	}

}
