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
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.renderer.legend.LegendFactory;

public class LayerModelTest extends TestCase {

	private DataSourceFactory dsf = new DataSourceFactory();
	private DataSource dummy;
	private DataSource dummy2;
	private DataSource dummy3;

	@Override
	protected void setUp() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver();
		dsf.getSourceManager().register("vector1", omd);
		dummy = dsf.getDataSource(omd);
		omd = new ObjectMemoryDriver();
		dsf.getSourceManager().register("vector2", omd);
		dummy2 = dsf.getDataSource("vector2");
		omd = new ObjectMemoryDriver();
		dsf.getSourceManager().register("vector3", omd);
		dummy3 = dsf.getDataSource("vector3");
		super.setUp();
	}

	public void testTreeExploring() throws Exception {

		ILayer vl = LayerFactory.createLayer((DataSource) dummy);
		ILayer rl = LayerFactory.createLayer("my tiff", new File("src/test/resources/ace.tif"));
		ILayer lc = LayerFactory.createLayerCollection("my data");
		lc.addLayer(vl);
		lc.addLayer(rl);

		ILayer layer = lc;
		if (layer instanceof LayerCollection) {
			lc = (ILayer) layer;
			lc.getChildren();
		} else {
			if (layer.getDataSource().isDefaultRaster()) {
				GeoRaster fc = layer.getDataSource().getRaster(0);
				assertTrue(fc != null);
			} else if (layer.getDataSource().isDefaultVectorial()) {
				DataSource fc = layer.getDataSource();
				assertTrue(fc != null);
			}
		}
	}

	public void testLayerEvents() throws Exception {
		TestLayerListener listener = new TestLayerListener();
		ILayer vl = LayerFactory.createLayer((DataSource) dummy);
		LayerCollection lc = LayerFactory.createLayerCollection("root");
		vl.addLayerListener(listener);
		lc.addLayerListener(listener);
		ILayer vl1 = LayerFactory.createLayer((DataSource) dummy);
		lc.addLayer(vl1);
		assertTrue(listener.la == 1);
		lc.setName("new name");
		assertTrue(listener.nc == 1);
		lc.setVisible(false);
		assertTrue(listener.vc == 1);
		vl.setLegend(LegendFactory.createUniqueSymbolLegend());
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
		ILayer vl1 = LayerFactory.createLayer(dummy);
		ILayer vl2 = LayerFactory.createLayer(dummy2);
		ILayer vl3 = LayerFactory.createLayer(dummy3);
		lc1.addLayer(vl1);
		lc2.addLayer(vl2);
		lc1.addLayer(lc2);
		lc3.addLayer(vl3);
		lc2.addLayer(lc3);
		try {
			vl3.setName(dummy2.getName());
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
		lc.addLayer(vl1);
		lc.addLayer(vl2);
		assertTrue(!vl1.getName().equals(vl2.getName()));

	}

	public void testAddToChild() throws Exception {
		ILayer lc1 = LayerFactory.createLayerCollection("firstLevel");
		ILayer lc2 = LayerFactory.createLayerCollection("secondLevel");
		ILayer lc3 = LayerFactory.createLayerCollection("thirdLevel");
		ILayer lc4 = LayerFactory.createLayerCollection("fourthLevel");
		lc1.addLayer(lc2);
		lc2.addLayer(lc3);
		lc3.addLayer(lc4);
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
		ILayer vl1 = LayerFactory.createLayer(dummy);
		lc.addLayer(l2);
		l2.addLayer(vl1);
		assertTrue(lc.containsLayerName(vl1.getName()));
	}

	public void testGetLayerByName() throws Exception {
		LayerCollection lc = LayerFactory.createLayerCollection("root");
		ILayer l2 = LayerFactory.createLayerCollection("secondlevel");
		ILayer l3 = LayerFactory.createLayerCollection("secondlevelbis");
		ILayer vl1 = LayerFactory.createLayer(dummy);
		l2.addLayer(vl1);
		lc.addLayer(l2);
		lc.addLayer(l3);

		assertTrue(lc.getLayerByName("secondlevel") == l2);
		assertTrue(lc.getLayerByName("secondlevelbis") == l3);
		assertTrue(lc.getLayerByName(dummy.getName()) == vl1);
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