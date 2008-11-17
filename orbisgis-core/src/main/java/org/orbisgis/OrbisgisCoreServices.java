package org.orbisgis;

import java.awt.Color;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.grap.lut.LutGenerator;
import org.orbisgis.configuration.BasicConfiguration;
import org.orbisgis.configuration.DefaultBasicConfiguration;
import org.orbisgis.errorManager.ErrorManager;
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
import org.orbisgis.renderer.symbol.ArrowSymbol;
import org.orbisgis.renderer.symbol.DefaultSymbolManager;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.renderer.symbol.SymbolManager;
import org.orbisgis.workspace.DefaultOGWorkspace;
import org.orbisgis.workspace.OGWorkspace;
import org.orbisgis.workspace.Workspace;

public class OrbisgisCoreServices {

	private static final String SOURCES_DIR_NAME = "sources";
	private final static Logger logger = Logger
			.getLogger(OrbisgisCoreServices.class);

	/**
	 * Installs all the OrbisGIS core services
	 */
	public static void installServices() {
		// Error service must be installed
		if (Services.getService(ErrorManager.class) == null) {
			throw new IllegalStateException("Error service must be installed "
					+ "before initializing OrbisGIS services");
		}
		if (Services.getService(ErrorManager.class) == null) {
			throw new IllegalStateException(
					"Workspace service must be installed "
							+ "before initializing OrbisGIS services");
		}

		installApplicationInfoServices();

		installWorkspaceServices();

		installConfigurationService();

		installSymbologyServices();

		installGeocognitionService();

		installJavaServices();

		installExportServices();
	}

	private static void installApplicationInfoServices() {
		if (Services.getService(ApplicationInfo.class) == null) {
			Services.registerService(ApplicationInfo.class,
					"Gets information about the application: "
							+ "name, version, etc.",
					new OrbisGISApplicationInfo());
		}
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

	/**
	 * Installs services that depend on the workspace such as the
	 * {@link DataManager}
	 */
	public static void installWorkspaceServices() {
		Workspace workspace = Services.getService(Workspace.class);

		DefaultOGWorkspace defaultOGWorkspace = new DefaultOGWorkspace();
		Services.registerService(OGWorkspace.class,
				"Gives access to directories inside the workspace."
						+ " You can use the temporal folder in "
						+ "the workspace through this service. It lets "
						+ "the access to the results folder",
				defaultOGWorkspace);

		File sourcesDir = workspace.getFile(SOURCES_DIR_NAME);
		if (!sourcesDir.exists()) {
			sourcesDir.mkdirs();
		}

		OGWorkspace ews = Services.getService(OGWorkspace.class);

		DataSourceFactory dsf = new DataSourceFactory(sourcesDir
				.getAbsolutePath(), ews.getTempFolder().getAbsolutePath());
		dsf.setResultDir(ews.getResultsFolder());

		// Pipeline the warnings in gdms to the warning system in the
		// application
		dsf.setWarninglistener(new PipelineWarningListener());

		// Installation of the service
		Services
				.registerService(
						DataManager.class,
						"Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
						new DefaultDataManager(dsf));
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
		sm.addSymbol(new ArrowSymbol(8, 6, Color.red, Color.black, 1));

		lm.addLegend(LegendFactory.createUniqueSymbolLegend());
		lm.addLegend(LegendFactory.createUniqueValueLegend());
		lm.addLegend(LegendFactory.createIntervalLegend());
		lm.addLegend(LegendFactory.createProportionalLegend());
		lm.addLegend(LegendFactory.createLabelLegend());
		lm.addLegend(new RasterLegend(LutGenerator.colorModel("gray"), 1));
	}

	private static void installConfigurationService() {
		BasicConfiguration bc = new DefaultBasicConfiguration();
		Services.registerService(BasicConfiguration.class,
				"Manages the basic configurations (key, value)", bc);
		bc.load();
	}

	private static final class PipelineWarningListener implements
			WarningListener {

		private String lastMessage = null;
		private boolean ignoredMsgShown = false;

		public void throwWarning(String msg) {
			if (shouldRepport(msg)) {
				Services.getService(ErrorManager.class).warning(msg, null);
			}
		}

		private boolean shouldRepport(String msg) {
			if (looksLikePrevious(msg)) {
				if (!ignoredMsgShown) {
					ignoredMsgShown = true;
					Services.getService(ErrorManager.class).warning(
							"Similar warnings ignored");
				}
				return false;
			} else {
				ignoredMsgShown = false;
				return true;
			}
		}

		public void throwWarning(String msg, Throwable t, Object source) {
			if (shouldRepport(msg)) {
				Services.getService(ErrorManager.class).warning(msg, t);
			}
		}

		private boolean looksLikePrevious(String currentMsg) {
			if (lastMessage == null) {
				lastMessage = currentMsg;
				return false;
			} else {
				String currentMsgStart = currentMsg.substring(0, currentMsg
						.length() / 4);
				String currentMsgEnd = currentMsg.substring((3 * currentMsg
						.length()) / 4);
				return (lastMessage.startsWith(currentMsgStart) || lastMessage
						.endsWith(currentMsgEnd));
			}
		}

	}
}
