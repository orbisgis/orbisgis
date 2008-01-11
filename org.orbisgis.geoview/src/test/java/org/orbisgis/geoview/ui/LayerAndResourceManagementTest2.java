/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
