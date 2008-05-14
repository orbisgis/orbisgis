package org.orbisgis;

import java.io.File;

import org.gdms.source.SourceManager;

public class FormatTest extends AbstractTest {

	private SourceManager sourceManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sourceManager = ((DataManager) Services.getService("org.orbisgis.DataManager")).getDSF().getSourceManager();
		sourceManager.removeAll();
	}

	public void testTiff() throws Exception {
		File file = new File("src/test/resources/ace.tif");
		sourceManager.register("tif", file);
		getDataManager().createLayer("tif");
		file = new File("src/test/resources/ace.tiff");
		sourceManager.register("tiff", file);
		getDataManager().createLayer("tiff");
	}

	public void testAsc() throws Exception {
		File file = new File("src/test/resources/3x3.asc");
		sourceManager.register("asc", file);
		getDataManager().createLayer("asc");
	}

	public void testShapefile() throws Exception {
		File file = new File("src/test/resources/bv_sap.shp");
		sourceManager.register("shp", file);
		getDataManager().createLayer("shp");
	}

}
