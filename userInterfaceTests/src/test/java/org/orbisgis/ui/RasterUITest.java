package org.orbisgis.ui;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.EPGeoviewActionHelper;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.rasterProcessing.toolbar.ImageCalculator;
import org.orbisgis.geoview.views.toc.EPTocLayerActionHelper;
import org.sif.UIFactory;

public class RasterUITest extends UITest {

	public void testImageCalculator() throws Exception {
		addLayer("sample.asc");
		UIFactory.setInputFor(ImageCalculator.DIALOG_ID, "substract_sample");
		EPGeoviewActionHelper.executeAction(geoview,
				"org.orbisgis.geoview.tools.rasterProcessing.ImageCalculator");
		assertTrue(viewContext.getLayers().length == 2);
		OrbisgisCore.getDSF().getSourceManager().removeAll();
	}


	public void testSlope() throws Exception{

		ILayer layer = addLayer("sample.asc");
		EPTocLayerActionHelper.execute(geoview,
		"org.orbisgis.geoview.rasterProcessing.action.terrainAnalysis.topography.ProcessSlopesInPercent",
		new ILayer[]{layer});
		assertTrue(viewContext.getLayers().length == 2);
		OrbisgisCore.getDSF().getSourceManager().removeAll();
	}
}
