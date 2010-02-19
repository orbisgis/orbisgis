package org.orbisgis.core.ui.views.geocognition;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.orbisgis.plugins.core.OrbisgisCoreServices;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.geocognition.DefaultGeocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.geocognition.GeocognitionFilter;
import org.orbisgis.plugins.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionView;
import org.orbisgis.plugins.core.workspace.TestWorkspace;
import org.orbisgis.plugins.core.workspace.Workspace;
import org.orbisgis.plugins.errorManager.DefaultErrorManager;
import org.orbisgis.plugins.errorManager.ErrorManager;

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
