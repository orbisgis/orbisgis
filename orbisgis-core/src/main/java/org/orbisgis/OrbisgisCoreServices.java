package org.orbisgis;

import java.awt.Color;

import org.grap.lut.LutGenerator;
import org.orbisgis.geocognition.DefaultGeocognition;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.javaManager.DefaultJavaManager;
import org.orbisgis.javaManager.JavaManager;
import org.orbisgis.renderer.legend.RasterLegend;
import org.orbisgis.renderer.legend.carto.DefaultLegendManager;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.LegendManager;
import org.orbisgis.renderer.symbol.DefaultSymbolManager;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.renderer.symbol.SymbolManager;
import org.orbisgis.workspace.DefaultOGWorkspace;
import org.orbisgis.workspace.OGWorkspace;

public class OrbisgisCoreServices {

	public static void installServices() {
		installSymbologyServices();

		installGeocognitionService();

		installWorkspaceService();

		installJavaServices();
	}

	public static void installJavaServices() {
		Services.registerService("org.orbisgis.JavaManager", JavaManager.class,
				"Execution of java code and java scripts",
				new DefaultJavaManager());
	}

	public static void installWorkspaceService() {
		DefaultOGWorkspace defaultOGWorkspace = new DefaultOGWorkspace();
		Services.registerService("org.orbisgis.OGWorkspace", OGWorkspace.class,
				"Gives access to directories inside the workspace."
						+ " You can use the temporal folder in "
						+ "the workspace through this service. It lets "
						+ "the access to the results folder",
				defaultOGWorkspace);
	}

	public static void installGeocognitionService() {
		DefaultGeocognition dg = new DefaultGeocognition();
		Services
				.registerService(
						"org.orbisgis.Geocognition",
						Geocognition.class,
						"Registry containing all the artifacts produced and shared by the users",
						dg);
	}

	public static void installSymbologyServices() {
		DefaultSymbolManager sm = new DefaultSymbolManager();
		Services.registerService("org.orbisgis.SymbolManager",
				SymbolManager.class,
				"Manages the list of available symbol types", sm);

		DefaultLegendManager lm = new DefaultLegendManager();
		Services.registerService("org.orbisgis.LegendManager",
				LegendManager.class,
				"Manages the list of available legend types", lm);

		sm.addSymbol(SymbolFactory.createPointCircleSymbol(Color.black, 1,
				Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createPointSquareSymbol(Color.black, 1,
				Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createSquareVertexSymbol(Color.black, 1,
				Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createCircleVertexSymbol(Color.black, 1,
				Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createPolygonCentroidSquareSymbol(
				Color.black, 1, Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createPolygonCentroidCircleSymbol(
				Color.black, 1, Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createPolygonSymbol());
		sm.addSymbol(SymbolFactory.createLineSymbol(Color.black, 1));
		sm.addSymbol(SymbolFactory.createImageSymbol());

		lm.addLegend(LegendFactory.createUniqueSymbolLegend());
		lm.addLegend(LegendFactory.createUniqueValueLegend());
		lm.addLegend(LegendFactory.createIntervalLegend());
		lm.addLegend(LegendFactory.createProportionalLegend());
		lm.addLegend(LegendFactory.createLabelLegend());
		lm.addLegend(new RasterLegend(LutGenerator.colorModel("gray"), 1));
	}
}
