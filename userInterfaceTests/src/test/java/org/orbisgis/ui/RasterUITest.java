package org.orbisgis.ui;

import org.orbisgis.geoview.EPGeoviewActionHelper;
import org.orbisgis.geoview.rasterProcessing.toolbar.ImageCalculator;
import org.sif.UIFactory;

public class RasterUITest extends UITest {

	public void testImageCalculator() throws Exception {
		addLayer("sample.asc");
		UIFactory.setInputFor(ImageCalculator.DIALOG_ID, "substract_sample");
		EPGeoviewActionHelper.executeAction(geoview,
				"org.orbisgis.geoview.tools.rasterProcessing.ImageCalculator");
		assertTrue(viewContext.getLayers().length == 2);
		clearCatalog();
	}
}
