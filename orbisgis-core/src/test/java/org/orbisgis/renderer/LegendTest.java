package org.orbisgis.renderer;

import java.awt.image.BufferedImage;
import java.io.File;

import org.orbisgis.AbstractTest;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class LegendTest extends AbstractTest {

	public void testSetLegend() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer1 = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer1);
		ILayer layer2 = getDataManager().createLayer(
				new File("src/test/resources/ace.tiff"));
		mc.getLayerModel().addLayer(layer2);
		Renderer r = new Renderer();
		BufferedImage img = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		r.draw(img, mc.getLayerModel().getEnvelope(), mc.getLayerModel());

		try {
			layer1.getRasterLegend();
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
		try {
			layer1.getRasterLegend("the_geom");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			layer2.getRasterLegend("rasterr");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		try {
			layer2.getVectorLegend();
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
		try {
			layer2.getVectorLegend("raster");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			layer1.getVectorLegend("thegeom");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}
}
