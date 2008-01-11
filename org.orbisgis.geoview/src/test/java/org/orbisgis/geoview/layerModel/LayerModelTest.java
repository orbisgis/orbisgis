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
package org.orbisgis.geoview.layerModel;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.orbisgis.core.OrbisgisCore;

public class LayerModelTest extends TestCase {

	public void testTreeExploring() throws Exception {

		VectorLayer vl = LayerFactory.createVectorialLayer("my shapefile",
				(DataSource) null);
		RasterLayer rl = LayerFactory.createRasterLayer("my tiff", null);
		ILayer lc = LayerFactory.createLayerCollection("my data");
		lc.put(vl);
		lc.put(rl);

		ILayer layer = lc;
		if (layer instanceof LayerCollection) {
			lc = (ILayer) layer;
			lc.getChildren();
		} else {
			if (layer instanceof VectorLayer) {
				DataSource fc = ((VectorLayer) layer).getDataSource();
				assertTrue(fc != null);
			} else if (layer instanceof RasterLayer) {
				GeoRaster fc = ((RasterLayer) layer).getGeoRaster();
				assertTrue(fc != null);
			} else if (layer instanceof TINLayer) {
				DataSource fc = ((TINLayer) layer).getDataSource();
				assertTrue(fc != null);
			}
		}
	}

	public void testLayerEvents() throws Exception {
		TestLayerListener listener = new TestLayerListener();
		VectorLayer vl = LayerFactory.createVectorialLayer("name",
				(DataSource) null);
		LayerCollection lc = LayerFactory.createLayerCollection("root");
		vl.addLayerListener(listener);
		lc.addLayerListener(listener);
		VectorLayer vl1 = LayerFactory.createVectorialLayer("vector",
				(DataSource) null);
		lc.put(vl1);
		assertTrue(listener.la == 1);
		lc.setName("new name");
		assertTrue(listener.nc == 1);
		lc.setVisible(false);
		assertTrue(listener.vc == 1);
		vl.setStyle(null);
		assertTrue(listener.sc == 1);
		lc.remove(vl1.getName());
		assertTrue(listener.lr == 1);
		assertTrue(lc.size() == 0);
	}

	public void testRepeatedName() throws Exception {
		DataSourceFactory dsf = OrbisgisCore.getDSF();
		SourceManager sourceManager = dsf.getSourceManager();
		sourceManager.register("vector1", new File("/tmp/1.shp"));
		sourceManager.register("vector2", new File("/tmp/2.shp"));
		sourceManager.register("vector3", new File("/tmp/3.shp"));
		ILayer lc1 = LayerFactory.createLayerCollection("firstLevel");
		ILayer lc2 = LayerFactory.createLayerCollection("secondLevel");
		ILayer lc3 = LayerFactory.createLayerCollection("thirdLevel");
		VectorLayer vl1 = LayerFactory.createVectorialLayer("vector1",
				(DataSource) null);
		VectorLayer vl2 = LayerFactory.createVectorialLayer("vector2",
				(DataSource) null);
		VectorLayer vl3 = LayerFactory.createVectorialLayer("vector3",
				(DataSource) null);
		lc1.put(vl1);
		lc2.put(vl2);
		lc1.put(lc2);
		lc3.put(vl3);
		lc2.put(lc3);
		try {
			vl3.setName("vector2");
			assertTrue(false);
		} catch (LayerException e) {
		}
		assertTrue(!vl3.getName().equals("vector2"));
		vl3.setName("firstLevel");
		assertTrue(!vl3.getName().equals("firstLevel"));
		lc1.setName("vector2");
		assertTrue(!lc1.getName().equals("vector2"));
	}

	public void testAddWithSameName() throws Exception {
		DataSourceFactory dsf = OrbisgisCore.getDSF();
		SourceManager sourceManager = dsf.getSourceManager();
		sourceManager
				.register("mySource", new File("src/test/resources/1.shp"));
		ILayer lc = LayerFactory.createLayerCollection("firstLevel");
		ILayer vl1 = LayerFactory.createLayer("mySource");
		ILayer vl2 = LayerFactory.createLayer("mySource");
		lc.put(vl1);
		lc.put(vl2);
		assertTrue(!vl1.getName().equals(vl2.getName()));

	}

	public void testAddToChild() throws Exception {
		ILayer lc1 = LayerFactory.createLayerCollection("firstLevel");
		ILayer lc2 = LayerFactory.createLayerCollection("secondLevel");
		ILayer lc3 = LayerFactory.createLayerCollection("thirdLevel");
		ILayer lc4 = LayerFactory.createLayerCollection("fourthLevel");
		lc1.put(lc2);
		lc2.put(lc3);
		lc3.put(lc4);
		try {
			lc2.moveTo(lc4);
			assertTrue(false);
		} catch (LayerException e) {
		}

		TestLayerListener listener = new TestLayerListener();
		lc1.addLayerListenerRecursively(listener);
		lc3.moveTo(lc1);
		assertTrue(lc3.getParent() == lc1);
		assertTrue(lc2.getChildren().length == 0);
		assertTrue(listener.la == 0);
		assertTrue(listener.lr == 0);
		assertTrue(listener.lm == 1);
	}

	public void testContainsLayer() throws Exception {
		LayerCollection lc = LayerFactory.createLayerCollection("root");
		ILayer l2 = LayerFactory.createLayerCollection("secondlevel");
		VectorLayer vl1 = LayerFactory.createVectorialLayer("vector",
				(DataSource) null);
		lc.put(l2);
		l2.put(vl1);
		assertTrue(lc.containsLayerName(vl1.getName()));
	}

	private class TestLayerListener implements LayerListener {

		private int nc = 0;

		private int vc = 0;

		private int la = 0;

		private int lm = 0;

		private int lr = 0;

		private int sc = 0;

		public void nameChanged(LayerListenerEvent e) {
			nc++;
		}

		public void visibilityChanged(LayerListenerEvent e) {
			vc++;
		}

		public void layerAdded(LayerCollectionEvent listener) {
			la++;
		}

		public void layerMoved(LayerCollectionEvent listener) {
			lm++;
		}

		public void layerRemoved(LayerCollectionEvent listener) {
			lr++;
		}

		public void styleChanged(LayerListenerEvent e) {
			sc++;
		}
	}
}