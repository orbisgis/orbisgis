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

import org.gdms.data.NoSuchTableException;
import org.gdms.source.SourceManager;
import org.orbisgis.CollectionUtils;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.views.toc.EPTocLayerActionHelper;

public class TocTest extends UITest {

	public void testAddLayer() throws Exception {
		// Assert toc is empty
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == true);

		// Add a vectorial layer
		addLayer("vectorial");
		// Add a raster layer
		addLayer("tif");

		// assert they have been added
		assertTrue(OrbisgisCore.getDSF().getSourceManager().isEmpty() == false);
		ILayer[] layers = viewContext.getViewModel().getChildren();
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[0].getName()) != null);
		assertTrue(OrbisgisCore.getDSF().getSourceManager().getSource(
				layers[1].getName()) != null);
	}

	public void testAddSQLLayer() throws Exception {
		String sql = "select * from " + viewContext.getLayers()[0].getName();
		String sourceName = "sqlResult";
		OrbisgisCore.getDSF().getSourceManager().register(sourceName, sql);
		ILayer layer = LayerFactory.createLayer(sourceName);
		viewContext.getViewModel().addLayer(layer);
		viewContext.getViewModel().remove(layer);
		OrbisgisCore.getDSF().getSourceManager().remove(sourceName);
	}

	public void testGroupLayers() throws Exception {
		ILayer[] layers = viewContext.getLayers();
		viewContext.setSelectedLayers(layers);

		EPTocLayerActionHelper.execute(geoview,
				"org.orbisgis.geoview.toc.GroupLayersAction", layers);

		ILayer[] children = viewContext.getViewModel().getChildren();
		ILayer group = children[0];
		assertTrue(children.length == 1);
		assertTrue(CollectionUtils.contains(group.getChildren(), layers[0]));
		assertTrue(CollectionUtils.contains(group.getChildren(), layers[1]));
		assertTrue(group.getChildren().length == 2);

		layers[0].moveTo(viewContext.getViewModel());
		layers[1].moveTo(viewContext.getViewModel());
		viewContext.getViewModel().remove(group);
	}

	public void testRename() throws Exception {
		// get the raster layer and it's name
		ILayer layer = viewContext.getViewModel().getChildren()[1];
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
		ILayer root = viewContext.getViewModel();
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
