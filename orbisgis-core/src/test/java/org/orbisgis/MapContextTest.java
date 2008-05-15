package org.orbisgis;

import java.io.File;

import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class MapContextTest extends AbstractTest {

	public void testRemoveSelectedLayer() throws Exception {
		MapContext vc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		vc.getLayerModel().addLayer(layer);
		vc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(vc.getSelectedLayers().length == 1);
		assertTrue(vc.getSelectedLayers()[0] == layer);
		vc.getLayerModel().remove(layer);
		assertTrue(vc.getSelectedLayers().length == 0);
	}

}
