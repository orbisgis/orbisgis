package org.orbisgis.plugin.view.layerModel;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.spatial.NullCRS;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class LayerModelTest extends TestCase {
	private CoordinateReferenceSystem crs;

	@Override
	protected void setUp() throws Exception {
		// crs = CRS.decode("EPSG:4326");
		// crs2 = CRS.decode("EPSG:27582");
		crs = NullCRS.singleton;
		super.setUp();
	}

	public void testTreeExploring() throws Exception {

		VectorLayer vl = new VectorLayer("my shapefile", crs);
		RasterLayer rl = new RasterLayer("my tiff", crs);
		LayerCollection lc = new LayerCollection("my data");
		lc.put(vl);
		lc.put(rl);

		ILayer layer = lc;
		if (layer instanceof LayerCollection) {
			lc = (LayerCollection) layer;
			lc.getLayers();
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
		VectorLayer vl = new VectorLayer("name", null);
		LayerCollection lc = new LayerCollection("root");
		vl.addLayerListener(listener);
		lc.addLayerListener(listener);
		lc.addCollectionListener(listener);
		VectorLayer vl1 = new VectorLayer("vector", crs);
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
		LayerCollection lc1 = new LayerCollection("firstLevel");
		LayerCollection lc2 = new LayerCollection("secondLevel");
		LayerCollection lc3 = new LayerCollection("thirdLevel");
		VectorLayer vl1 = new VectorLayer("vector1", crs);
		VectorLayer vl2 = new VectorLayer("vector2", crs);
		VectorLayer vl3 = new VectorLayer("vector3", crs);
		lc1.put(vl1);
		lc2.put(vl2);
		lc1.put(lc2);
		lc3.put(vl3);
		lc2.put(lc3);
		vl3.setName("vector2");
		assertTrue(!vl3.getName().equals("vector2"));
		vl3.setName("firstLevel");
		assertTrue(!vl3.getName().equals("firstLevel"));
		lc1.setName("vector2");
		assertTrue(!lc1.getName().equals("vector2"));
	}

	public void testContainsLayer() throws Exception {
		LayerCollection lc = new LayerCollection("root");
		LayerCollection l2 = new LayerCollection("secondlevel");
		VectorLayer vl1 = new VectorLayer("vector", crs);
		lc.put(l2);
		l2.put(vl1);
		assertTrue(lc.containsLayerName(vl1.getName()));
	}

	private class TestLayerListener implements LayerListener,
			LayerCollectionListener {

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