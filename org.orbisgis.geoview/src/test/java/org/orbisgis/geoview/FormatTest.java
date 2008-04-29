package org.orbisgis.geoview;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.source.SourceManager;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.LayerFactory;

public class FormatTest extends TestCase {

	private SourceManager sourceManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sourceManager = OrbisgisCore.getDSF().getSourceManager();
		sourceManager.removeAll();
	}

	public void testTiff() throws Exception {
		File file = new File("src/test/resources/ace.tif");
		sourceManager.register("tif", file);
		LayerFactory.createLayer("tif");
		file = new File("src/test/resources/ace.tiff");
		sourceManager.register("tiff", file);
		LayerFactory.createLayer("tiff");
	}

	public void testAsc() throws Exception {
		File file = new File("src/test/resources/3x3.asc");
		sourceManager.register("asc", file);
		LayerFactory.createLayer("asc");
	}

	public void testShapefile() throws Exception {
		File file = new File("src/test/resources/bv_sap.shp");
		sourceManager.register("shp", file);
		LayerFactory.createLayer("shp");
	}

}
