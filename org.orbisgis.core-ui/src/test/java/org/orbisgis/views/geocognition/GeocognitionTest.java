package org.orbisgis.views.geocognition;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

import org.orbisgis.OrbisgisCoreServices;
import org.orbisgis.Services;
import org.orbisgis.errorManager.DefaultErrorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.geocognition.DefaultGeocognition;
import org.orbisgis.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.geocognition.sql.Code;
import org.orbisgis.geocognition.sql.CustomQueryJavaCode;
import org.orbisgis.geocognition.sql.FunctionJavaCode;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.geocognition.sql.GeocognitionJavaFunction;
import org.orbisgis.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.workspace.TestWorkspace;
import org.orbisgis.workspace.Workspace;

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

	public void testDefaultFunctionCompiles() throws Exception {
		File file = new File("src/main/resources/" + "org/orbisgis/views/"
				+ "geocognition/wizards/NewJavaFunction");
		String content = getContent(file);
		testDefaultArtifactCompiles(file, new FunctionJavaCode(content));
	}

	public void testDefaultCustomQueryCompiles() throws Exception {
		File file = new File("src/main/resources/" + "org/orbisgis/views/"
				+ "geocognition/wizards/NewJavaCustomQuery");
		String content = getContent(file);
		testDefaultArtifactCompiles(file, new CustomQueryJavaCode(content));
	}

	private void testDefaultArtifactCompiles(File file, Code code)
			throws FileNotFoundException, IOException {

		gc.addElement("A", code);
		Map<String, String> props = gc.getGeocognitionElement("A")
				.getProperties();
		assertTrue(props.get(GeocognitionJavaFunction.COMPILE_RESULT).equals(
				GeocognitionJavaFunction.COMPILE_OK));
		gc.removeElement("A");
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
