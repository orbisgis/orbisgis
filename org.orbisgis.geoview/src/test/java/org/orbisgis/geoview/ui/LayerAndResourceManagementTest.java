/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
import org.orbisgis.geocatalog.EPGeocatalogResourceActionHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.views.toc.EPTocLayerActionHelper;

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

		saveAndLoad();
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

		// Create a subgroup in the group
		EPTocLayerActionHelper.execute(geoview,
				"org.orbisgis.geoview.toc.CreateGroupAction",
				new ILayer[] { group });
		ILayer group2 = group.getChildren()[0];
		assertTrue(group2.getParent() == group);

		// Move layer to subgroup
		viewContext.setSelectedLayers(new ILayer[] { layer });
		trans = toc.getDragData(null);
		toc.doDrop(trans, group2);

		// Move parent group to layer
		viewContext.setSelectedLayers(new ILayer[] { group });
		trans = toc.getDragData(null);
		toc.doDrop(trans, layer);

		// Assert nothing happens
		assertTrue(layer.getParent() == group2);
		assertTrue(group2.getParent() == group);
		assertTrue(group.getChildren()[0] == group2);
		assertTrue(group.getParent() == viewContext.getRootLayer());
		assertTrue(viewContext.getRootLayer().getLayersRecursively().length == 4);

		// Move layer to root
		viewContext.setSelectedLayers(new ILayer[] { layer });
		trans = toc.getDragData(null);
		toc.doDrop(trans, null);

		// Remove groups
		viewContext.getRootLayer().remove(group);
		viewContext.getRootLayer().remove(group2);
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

}
