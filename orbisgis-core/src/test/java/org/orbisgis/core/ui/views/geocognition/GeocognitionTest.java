package org.orbisgis.core.ui.views.geocognition;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;

import org.orbisgis.core.Services;
import org.orbisgis.core.OrbisgisCoreServices;
import org.orbisgis.core.geocognition.DefaultGeocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionFilter;
import org.orbisgis.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.core.ui.views.geocognition.GeocognitionView;
import org.orbisgis.core.workspace.TestWorkspace;
import org.orbisgis.errorManager.DefaultErrorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.core.workspace.Workspace;

public class GeocognitionTest extends TestCase {

	private DefaultGeocognition gc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Services.registerService(ErrorManager.class, "",
				new DefaultErrorManager());
		TestWorkspace workspace = new TestWorkspace();
		workspace.setWorkspaceFolder("target");
		Services.registerService(Workspace.class, "", workspace);
		OrbisgisCoreServices.installServices();

		gc = new DefaultGeocognition();
		gc.addElementFactory(new GeocognitionSymbolFactory());
		gc.addElementFactory(new GeocognitionFunctionFactory());
		gc.addElementFactory(new GeocognitionCustomQueryFactory());
		gc.addElementFactory(new GeocognitionLegendFactory());
		gc.addElementFactory(new GeocognitionMapContextFactory());
	}

	public void testLoadAndCheckInitialGeocognition() throws Exception {
		InputStream geocognitionStream = GeocognitionView.class
				.getResourceAsStream(GeocognitionView.STARTUP_GEOCOGNITION_XML);
		gc.read(geocognitionStream);
		GeocognitionElement[] elems = gc.getElements(new GeocognitionFilter() {

			@Override
			public boolean accept(GeocognitionElement element) {
				return true;
			}
		});

		for (GeocognitionElement elem : elems) {
			assertTrue(elem.getObject() != null);
		}
	}

	public void testLoadInitialMap() throws Exception {
		InputStream geocognitionStream = GeocognitionView.class
				.getResourceAsStream(GeocognitionView.STARTUP_GEOCOGNITION_XML);
		gc.read(geocognitionStream);
		assertTrue(gc.getGeocognitionElement(GeocognitionView.FIRST_MAP) != null);
	}



	private String getContent(File file) throws FileNotFoundException,
			IOException {
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		String content = new String(buffer);
		return content;
	}
}
