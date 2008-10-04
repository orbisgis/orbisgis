package org.orbisgis;

import java.awt.Color;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.grap.lut.LutGenerator;
import org.orbisgis.configuration.BasicConfiguration;
import org.orbisgis.configuration.DefaultBasicConfiguration;
import org.orbisgis.geocognition.DefaultGeocognition;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.javaManager.DefaultJavaManager;
import org.orbisgis.javaManager.JavaManager;
import org.orbisgis.map.export.DefaultMapExportManager;
import org.orbisgis.map.export.MapExportManager;
import org.orbisgis.map.export.RectanglesScale;
import org.orbisgis.map.export.SingleLineScale;
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

	private final static Logger logger = Logger
			.getLogger(OrbisgisCoreServices.class);

	public static void installServices() {
		installSymbologyServices();

		installGeocognitionService();

		installWorkspaceService();

		installJavaServices();

		installExportServices();
	}

	private static void installExportServices() {
		DefaultMapExportManager mem = new DefaultMapExportManager();
		Services.registerService(MapExportManager.class,
				"Manages the export of MapContexts to different formats.", mem);
		mem.registerScale(SingleLineScale.class);
		mem.registerScale(RectanglesScale.class);
	}

	public static void installJavaServices() {
		HashSet<File> buildPath = new HashSet<File>();
		ClassLoader cl = OrbisgisCoreServices.class.getClassLoader();
		while (cl != null) {
			if (cl instanceof URLClassLoader) {
				URLClassLoader loader = (URLClassLoader) cl;
				URL[] urls = loader.getURLs();
				for (URL url : urls) {
					try {
						File file = new File(url.toURI());
						buildPath.add(file);
					} catch (URISyntaxException e) {
						logger.error("Cannot add classpath url: " + url, e);
					}
				}
			}
			cl = cl.getParent();
		}

		DefaultJavaManager javaManager = new DefaultJavaManager();
		Services.registerService(JavaManager.class,
				"Execution of java code and java scripts", javaManager);
		javaManager.addFilesToClassPath(Arrays.asList(buildPath
				.toArray(new File[0])));
	}

	public static void installWorkspaceService() {
		DefaultOGWorkspace defaultOGWorkspace = new DefaultOGWorkspace();
		Services.registerService(OGWorkspace.class,
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
						Geocognition.class,
						"Registry containing all the artifacts produced and shared by the users",
						dg);
	}

	public static void installSymbologyServices() {
		DefaultSymbolManager sm = new DefaultSymbolManager();
		Services.registerService(SymbolManager.class,
				"Manages the list of available symbol types", sm);

		DefaultLegendManager lm = new DefaultLegendManager();
		Services.registerService(LegendManager.class,
				"Manages the list of available legend types", lm);

		sm.addSymbol(SymbolFactory.createPointCircleSymbol(Color.black, 1,
				Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createPointSquareSymbol(Color.black, 1,
				Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createVertexCircleSymbol(Color.black, 1,
				Color.red, 10, false));
		sm.addSymbol(SymbolFactory.createVertexSquareSymbol(Color.black, 1,
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

	public static void installConfigurationService() {
		BasicConfiguration bc = new DefaultBasicConfiguration();
		Services.registerService(BasicConfiguration.class,
				"Manages the basic configurations (key, value)", bc);
		bc.load();
	}
}
