package org.orbisgis.core;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import static org.junit.Assert.*;

/**
 *
 */
public class OwsMapContextTest extends AbstractTest  {
    
       
	@Override
        @Before
	public void setUp() throws Exception {
		super.setUp();
		AbstractTest.registerDataManager();
	}
        
        
        @Test
	public void testRemoveSelectedLayer() throws Exception {
		MapContext mc = new OwsMapContext();
		mc.open(null);
		ILayer layer = mc.createLayer(
                        getDataSourceFromPath("src/test/resources/data/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
		assertTrue(mc.getSelectedLayers()[0] == layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getSelectedLayers().length == 0);
		mc.close(null);
	}

}
