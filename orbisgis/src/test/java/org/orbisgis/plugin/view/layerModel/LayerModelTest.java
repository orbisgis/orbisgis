package org.orbisgis.plugin.view.layerModel;

import junit.framework.TestCase;

import org.gdms.data.InternalDataSource;
import org.geotools.referencing.CRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.view.layerModel.CRSException;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerCollectionListener;
import org.orbisgis.plugin.view.layerModel.LayerListener;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.TINLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

public class LayerModelTest extends TestCase {
	private CoordinateReferenceSystem crs;

	private CoordinateReferenceSystem crs2;

	@Override
	protected void setUp() throws Exception {
		crs = CRS.decode("EPSG:4326");
		crs2 = CRS.decode("EPSG:27582");
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
				InternalDataSource fc = ((VectorLayer) layer).getDataSource();
				assertTrue(fc != null);
			} else if (layer instanceof RasterLayer) {
				GridCoverage fc = ((RasterLayer) layer).getGridCoverage();
				assertTrue(fc != null);
			} else if (layer instanceof TINLayer) {
				InternalDataSource fc = ((TINLayer) layer).getDataSource();
				assertTrue(fc != null);
			}
		}
	}

	public void testCRSMismatch() throws Exception {
		LayerCollection lc = new LayerCollection("root");
		VectorLayer vl1 = new VectorLayer("vector", crs);
		VectorLayer vl2 = new VectorLayer("vector", crs2);
		lc.put(vl1);
		try {
			lc.put(vl2);
			assertTrue(false);
		} catch (CRSException e) {
			assertTrue(true);
		}
	}

	public void testLayerEvents() throws Exception {
		TestLayerListener listener = new TestLayerListener();
		LayerCollection lc = new LayerCollection("root");
		lc.addLayerListener(listener);
		lc.addCollectionListener(listener);
		VectorLayer vl1 = new VectorLayer("vector", crs);
		lc.put(vl1);
		assertTrue(listener.la == 1);
		lc.setName("new name");
		assertTrue(listener.nc == 1);
		lc.setVisible(false);
		assertTrue(listener.vc == 1);
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

	}

}
