package org.orbisgis;

import java.io.File;

import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class MapContextTest extends AbstractTest {

	public void testRemoveSelectedLayer() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
		assertTrue(mc.getSelectedLayers()[0] == layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getSelectedLayers().length == 0);
	}

	public void testSetBadLayerSelection() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		ILayer layer2 = getDataManager().createLayer(
				new File("src/test/resources/1.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer2 });
		assertTrue(mc.getSelectedLayers().length == 0);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
	}

	public void testRemoveActiveLayer() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.setActiveLayer(layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getActiveLayer() == null);
	}

}
