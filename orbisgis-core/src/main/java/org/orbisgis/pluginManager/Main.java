/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 *
 *   Part of this code has been imported from GELAT a fork of OrbisGIS.
 *
 */

package org.orbisgis.pluginManager;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Timer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.utils.XMLUtils;
import org.orbisgis.core.workspace.Workspace;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class);
	private static CommonClassLoader commonClassLoader;
	private static boolean doc = false;
	private static File pluginListFile;
	private static boolean clean = false;
	private static String commandLineWorkspace = null;
	private static String[] pluginList = null;

	public static void main(String[] args) throws Exception {
		DefaultPluginManager pluginManager = new DefaultPluginManager();
		pluginManager.installServices();

		parseArguments(args);

		PropertyConfigurator.configure(Main.class
				.getResource("log4j.properties"));

		Splash splash = new Splash();
		splash.setVisible(true);

		try {
			ArrayList<PluginInfo> pluginDirs;
			if (pluginList != null) {
				pluginDirs = new ArrayList<PluginInfo>();
				for (String pluginResource : pluginList) {
					pluginDirs.add(new ResourcePluginInfo(pluginResource));
				}
			} else {
				pluginDirs = getPluginsInfo(pluginListFile);
			}

			Toolkit.getDefaultToolkit().getSystemEventQueue().push(
					new OrbisgisEventQueue());

			commonClassLoader = new CommonClassLoader();

			ArrayList<PluginInfo> plugins = createExtensionRegistry(pluginDirs,
					splash);

			PatternLayout l = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
			RollingFileAppender fa = new RollingFileAppender(l, Services
					.getService(ApplicationInfo.class).getLogFile());
			fa.setMaxFileSize("256KB");
			Logger.getRootLogger().addAppender(fa);

			pluginManager.setPlugins(plugins);

			Workspace ws = Services.getService(Workspace.class);
			if (commandLineWorkspace != null) {
				ws.setWorkspaceFolder(commandLineWorkspace);
			}
			ws.init(clean);

			pluginManager.start();

			if (doc) {
				Services.generateDoc(new File(getReferenceFile(),
						"services.html"));
			}
		} catch (Exception e) {
			splash.setVisible(false);
			splash.dispose();
			throw e;
		}

		splash.setVisible(false);
		splash.dispose();
	}

	private static void parseArguments(String[] args) throws IOException {
		pluginListFile = new File("./plugin-list.xml");
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-document")) {
				doc = true;
			} else if (args[i].equals("-p")) {
				pluginListFile = new File(args[i + 1]);
				i++;
			} else if (args[i].equals("-pl")) {
				pluginListFile = null;
				pluginList = args[i + 1].split("\\Q,\\E");
				i++;
			} else if (args[i].equals("-w")) {
				commandLineWorkspace = args[i + 1];
				i++;
			} else if (args[i].equals("-clean")) {
				clean = true;
			} else {
				System.err
						.println("usage Usage: java org.orbisgis.pluginManager.Main [-clean] [-document] [-p plugin-list.xml] [-w workspace-dir]");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<PluginInfo> createExtensionRegistry(
			ArrayList<PluginInfo> pluginDirs, Splash splash) throws Exception {
		ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
		HashMap<String, ExtensionPoint> extensionPoints = new HashMap<String, ExtensionPoint>();
		ArrayList<Extension> extensions = new ArrayList<Extension>();
		HashSet<String> extensionIds = new HashSet<String>();
		String workspaceClassName = null;
		for (PluginInfo pluginInfo : pluginDirs) {
			pluginInfo.setPluginClassLoader(commonClassLoader);

			byte[] pluginXML = pluginInfo.getDescriptorStream();
			VTD vtd = new VTD(pluginXML);

			// Modify class path
			String[] isolatedJars = new String[vtd
					.evalToInt("count(/plugin/isolated)")];
			for (int i = 0; i < isolatedJars.length; i++) {
				isolatedJars[i] = vtd.getContent("/plugin/isolated[" + (i + 1)
						+ "]");
			}
			// update classloader if there is some isolated jar
			if (isolatedJars.length != 0) {
				pluginInfo
						.setPluginClassLoader(getIsolatedClassLoader(isolatedJars));
			}
			updateCommonClassLoader(pluginInfo, isolatedJars);

			// Check for workspace
			if (vtd.count("/plugin/workspace") > 0) {
				workspaceClassName = vtd.getAttribute("/plugin/workspace",
						"class");
			}

			// Check for application info
			if (vtd.count("/plugin/application") > 0) {
				String infoClass = vtd.getAttribute("/plugin/application",
						"infoClass");
				try {
					Class<? extends ApplicationInfo> applicationInfoClass = (Class<? extends ApplicationInfo>) commonClassLoader
							.loadClass(infoClass);
					ApplicationInfo applicationInfo = applicationInfoClass
							.newInstance();
					Services.registerService(ApplicationInfo.class,
							"Gets information about the application: "
									+ "name, version, etc.", applicationInfo);
					splash.updateVersion();
				} catch (NumberFormatException e) {
					throw new Exception("Unrecognized " + "workspace version",
							e);
				}
			}

			//logger.debug("Processing plugin " + pluginInfo);


			splash.updateText("Loading plugin " + pluginInfo);
			// Check for extension points
			int n = vtd.evalToInt("count(/plugin/extension-point)");
			for (int i = 0; i < n; i++) {
				String condition = "" + (i + 1);
				ExtensionPoint ep = getExtensionPoint(pluginInfo, vtd,
						condition);
				extensionPoints.put(ep.getId(), ep);
			}

			// indirect extension points
			n = vtd.evalToInt("count(/plugin/extension-point-container)");
			for (int i = 0; i < n; i++) {
				String resource = vtd.getAttribute(
						"/plugin/extension-point-container[" + (i + 1) + "]",
						"resource");
				logger.debug("Processing extension point container: "
						+ resource);
				ResourcePluginInfo externalPluginInfo = new ResourcePluginInfo(
						resource);
				externalPluginInfo.setPluginClassLoader(commonClassLoader);
				VTD referedVTD = new VTD(externalPluginInfo
						.getDescriptorStream());
				int m = vtd
						.evalToInt("count(/plugin/extension-point-container["
								+ (i + 1) + "]/" + "extension-point)");
				for (int j = 0; j < m; j++) {
					String id = vtd
							.getAttribute("/plugin/extension-point-container["
									+ (i + 1) + "]/" + "extension-point["
									+ (j + 1) + "]", "id");
					ExtensionPoint ep = getExtensionPoint(externalPluginInfo,
							referedVTD, "@id='" + id + "'");
					extensionPoints.put(ep.getId(), ep);
				}

			}

			// Check for extensions
			n = vtd.evalToInt("count(/plugin/extension)");
			for (int i = 0; i < n; i++) {
				String condition = "" + (i + 1);
				Extension e = getExtension(pluginInfo, vtd, condition);
				if (e.getId() == null) {
					throw new RuntimeException("Extension 'id' attribute"
							+ " is mandatory: " + e.getXml());
				} else if (extensionIds.contains(e.getId())) {
					throw new RuntimeException("There is already an "
							+ "extension with such an id:" + e.getId());
				}
				extensionIds.add(e.getId());
				extensions.add(e);
			}

			// indirect extension
			n = vtd.evalToInt("count(/plugin/extension-container)");
			for (int i = 0; i < n; i++) {
				String resource = vtd.getAttribute(
						"/plugin/extension-container[" + (i + 1) + "]",
						"resource");
				logger.debug("Processing extension container: " + resource);
				ResourcePluginInfo externalPluginInfo = new ResourcePluginInfo(
						resource);
				externalPluginInfo.setPluginClassLoader(commonClassLoader);
				VTD referedVTD = new VTD(externalPluginInfo
						.getDescriptorStream());
				int m = vtd.evalToInt("count(/plugin/extension-container["
						+ (i + 1) + "]/" + "extension)");
				for (int j = 0; j < m; j++) {
					String id = vtd.getAttribute("/plugin/extension-container["
							+ (i + 1) + "]/" + "extension[" + (j + 1) + "]",
							"id");
					Extension e = getExtension(externalPluginInfo, referedVTD,
							"@id='" + id + "'");
					extensions.add(e);
				}

			}

			String activatorClassName = vtd
					.evalToString("/plugin/activator/@class");
			if (activatorClassName.equals("")) {
				activatorClassName = null;
			}

			pluginInfo.setActivatorClassName(activatorClassName);
			plugins.add(pluginInfo);
		}

		// install workspace
		if (workspaceClassName != null) {
			Class<?> workspaceInstance = commonClassLoader
					.loadClass(workspaceClassName);
			Services.registerService(Workspace.class,
					"Change workspace, save files in the workspace, etc.",
					workspaceInstance.newInstance());
		} else {
			throw new RuntimeException("No workspace found");
		}

		// Extensions
		resolveExtensions(extensions, extensionPoints);

		RegistryFactory.createExtensionRegistry(extensions, extensionPoints);

		if (doc) {

			generateSchemaDocumentation(extensionPoints, extensions);
			generateIndex(extensionPoints);
		}

		return plugins;
	}

	private static ExtensionPoint getExtensionPoint(PluginInfo pluginInfo,
			VTD vtd, String condition) throws Exception {
		String schema = vtd.getAttribute("/plugin/extension-point[" + condition
				+ "]", "schema");
		if (schema == null) {
			throw new Exception(
					"Could not find extension-point matching condition: "
							+ condition);
		}
		String schemaFile = pluginInfo.getRelativeSchema(schema);
		String id = vtd.getAttribute("/plugin/extension-point[" + condition
				+ "]", "id");

		ExtensionPoint ep = new ExtensionPoint(schemaFile, id);
		return ep;
	}

	private static Extension getExtension(PluginInfo pluginInfo, VTD vtd,
			String condition) throws Exception {
		String xpathExpr = "/plugin/extension[" + condition + "]";
		String point = vtd.getAttribute(xpathExpr, "point");
		if (point == null) {
			throw new Exception("Cannnot locate an extension that matches: "
					+ condition);
		}
		if (vtd.evalToInt("count(" + xpathExpr + ")") > 1) {
			throw new Exception("Two extensions matching the condition: "
					+ condition);
		}
		String xml = vtd.getContent(xpathExpr);
		String id = vtd.getAttribute(xpathExpr, "id");

		Extension e = new Extension(xml, point, id, pluginInfo);
		return e;
	}

	private static void generateIndex(
			HashMap<String, ExtensionPoint> extensionPoints)
			throws IOException, TransformerException {
		Iterator<String> epIterator = extensionPoints.keySet().iterator();
		StringBuffer epList = new StringBuffer();
		epList.append("<extension-points>");
		while (epIterator.hasNext()) {
			String epId = epIterator.next();
			epList.append("<extension-point id=\"" + epId + "\" href=\"" + epId
					+ ".html\"/>");
		}
		epList.append("</extension-points>");
		TransformerFactory transFact = TransformerFactory.newInstance();
		StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(
				epList.toString().getBytes()));
		InputStream is = Main.class
				.getResourceAsStream("/generate-index-documentation.xsl");
		StreamSource xsltSource = new StreamSource(is);

		Transformer trans = transFact.newTransformer(xsltSource);
		trans.transform(xmlSource, new StreamResult(new File(
				getReferenceFile(), "index.html")));
	}

	private static File getReferenceFile() throws IOException {
		File ret = new File("docs/reference");
		if (!ret.exists()) {
			if (!ret.mkdirs()) {
				throw new IOException("Cannot create documentation directory");
			}
		}

		return ret;
	}

	private static void generateSchemaDocumentation(
			HashMap<String, ExtensionPoint> extensionPoints,
			ArrayList<Extension> extensions) throws Exception {
		Iterator<String> epIterator = extensionPoints.keySet().iterator();
		while (epIterator.hasNext()) {
			String epId = epIterator.next();
			ExtensionPoint ep = extensionPoints.get(epId);
			String schemaContent = ep.getSchema();
			InputStream templateStream = Main.class
					.getResourceAsStream("/schema-template.xml");
			String template = new String(FileUtils.getContent(templateStream));
			template = template.replaceAll("\\Q[CONTENT]\\E",
					getSchemaContent(schemaContent));
			template = template.replaceAll("\\Q[EXTENSION_POINT_ID]\\E", ep
					.getId());
			template = template.replaceAll("\\Q[EXAMPLE]\\E",
					getFirstExtensionXML(epId, extensions));

			byte[] bytes = template.getBytes();
			TransformerFactory transFact = TransformerFactory.newInstance();
			StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(
					bytes));
			InputStream is = Main.class
					.getResourceAsStream("/generate-schema-documentation.xsl");
			StreamSource xsltSource = new StreamSource(is);

			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, new StreamResult(new File(
					getReferenceFile(), epId + ".html")));
		}
	}

	private static String getSchemaContent(String schemaContent)
			throws Exception {
		VTD vtd = new VTD(schemaContent.getBytes(), true);
		vtd.declareXPathNameSpace("xsd", "http://www.w3.org/2001/XMLSchema");
		return vtd.getContent("/xsd:schema/*");
	}

	private static String getFirstExtensionXML(String epId,
			ArrayList<Extension> extensions) {
		for (Extension extension : extensions) {
			if (extension.getPoint().equals(epId)) {
				return extension.getXml();
			}
		}

		return "No Example";
	}

	private static void updateCommonClassLoader(PluginInfo pluginInfo,
			String[] isolatedJars) throws IOException {
		commonClassLoader.addJars(pluginInfo.getJars());
		commonClassLoader.addOutputFolders(pluginInfo.getOutputFolders());
	}

	private static CommonClassLoader getIsolatedClassLoader(
			String[] isolatedJars) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	private static void resolveExtensions(ArrayList<Extension> extensions,
			HashMap<String, ExtensionPoint> extensionPoints) throws Exception {
		for (Extension extension : extensions) {
			ExtensionPoint extensionPoint = extensionPoints.get(extension
					.getPoint());
			if (extensionPoint == null) {
				throw new Exception("Extension point " + extension.getPoint()
						+ " not found. In extension " + extension.getId()
						+ " in " + extension.getPluginInfo());
			}
			String error = XMLUtils.validateXML(extensionPoint.getSchema(),
					extension.getXml());
			if (error != null) {
				throw new Exception(
						"The content of the extension does not match the schema. Extension: "
								+ extension.getId() + " in plugin "
								+ extension.getPluginInfo() + ": " + error);
			}
			extensionPoint.addExtension(extension);
		}
	}

	private static ArrayList<PluginInfo> getPluginsInfo(File pluginList)
			throws FileNotFoundException, IOException, EncodingException,
			EOFException, EntityException, ParseException, XPathParseException,
			XPathEvalException, NavException {
		logger.debug("Reading plugin list");
		ArrayList<PluginInfo> pluginDirs = new ArrayList<PluginInfo>();
		if (pluginList.exists()) {
			VTD vtd = new VTD(pluginList);
			int n = vtd.evalToInt("count(/plugins/plugin)");
			for (int i = 0; i < n; i++) {
				String pluginDir = vtd.getAttribute("/plugins/plugin["
						+ (i + 1) + "]", "dir");
				if (pluginDir != null) {
					String conf = vtd.getAttribute("/plugins/plugin[" + (i + 1)
							+ "]", "configuration");
					logger.debug("Plugin: " + pluginDir);
					pluginDirs.add(new DirPluginInfo(pluginDir, conf));
				} else {
					String confResource = vtd.getAttribute("/plugins/plugin["
							+ (i + 1) + "]", "conf-resource");
					if (confResource != null) {
						logger.debug("Resource plugin: " + confResource);
						pluginDirs.add(new ResourcePluginInfo(confResource));
					} else {
						throw new IllegalArgumentException(
								"Missing 'dir' attribute in " + "plugin tag");
					}
				}
			}
		} else {
			File pluginsDir = new File("./plugins");
			if (pluginsDir.exists()) {
				File[] dirs = pluginsDir.listFiles(new FileFilter() {

					public boolean accept(File pathname) {
						return (pathname.isDirectory());
					}

				});
				for (File pluginDir : dirs) {
					logger.debug("Plugin: " + pluginDir);
					pluginDirs.add(new DirPluginInfo(pluginDir
							.getAbsolutePath(), "."));
				}
			} else {
				throw new IllegalArgumentException(
						"No plugin list specified and "
								+ "no 'plugins' directory under working directory");
			}
		}
		return pluginDirs;
	}

	private static final class OrbisgisEventQueue extends EventQueue {

		private RefreshTimer timer = new RefreshTimer();

		private HashSet<Class<? extends AWTEvent>> meaningfulEvents = new HashSet<Class<? extends AWTEvent>>();

		public OrbisgisEventQueue() {
			meaningfulEvents.add(ActionEvent.class);
			meaningfulEvents.add(ComponentEvent.class);
			meaningfulEvents.add(WindowEvent.class);
			meaningfulEvents.add(KeyEvent.class);
			meaningfulEvents.add(MouseEvent.class);
			meaningfulEvents.add(InputMethodEvent.class);
		}

		@Override
		protected void dispatchEvent(AWTEvent event) {
			try {
				super.dispatchEvent(event);
				if (meaningfulEvents.contains(event.getClass())) {
					timer.start();
				}
			} catch (Exception e) {
				Services.getErrorManager().error(e.getMessage(), e);
			} catch (OutOfMemoryError e) {
				Services.getErrorManager().error(
						"Out of memory error. It's "
								+ "strongly recomended to "
								+ "restart the application", e);
			} catch (Throwable e) {
				Services.getErrorManager().error(e.getMessage(), e);
			}
		}
	}

	private static class RefreshTimer {

		private Timer timer;
		private ActionListener actionListener;

		public RefreshTimer() {
			actionListener = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					synchronized (RefreshTimer.class) {
						try {
							DefaultPluginManager.fireEvent();
						} catch (Throwable e1) {
							logger.error("Bug handling event!", e1);
						}
						timer.stop();
					}
				}

			};
			timer = new Timer(200, actionListener);
			timer.setCoalesce(true);
			timer.setInitialDelay(200);
			timer.setDelay(200);
		}

		public void start() {
			synchronized (RefreshTimer.class) {
				if (!timer.isRunning()) {
					timer.start();
				}
			}
		}

	}

	private static class DirPluginInfo extends Plugin implements PluginInfo {
		private String dir;
		private String conf;
		private PluginClassPathReader reader;

		public DirPluginInfo(String dir, String confFolder) {
			this.dir = dir;
			this.conf = confFolder;
			if (conf == null) {
				conf = ".";
			}
		}

		@Override
		public byte[] getDescriptorStream() throws IOException {
			return FileUtils.getContent(getDescriptorFile());
		}

		private File getDescriptorFile() {
			return new File(getConfigurationFolder(), "plugin.xml");
		}

		@Override
		public String toString() {
			return dir + ": " + conf;
		}

		@Override
		public String getRelativeSchema(String schema) throws IOException {
			File schemaFile = new File(getConfigurationFolder(), schema);
			return new String(FileUtils.getContent(schemaFile));
		}

		private File getConfigurationFolder() {
			return new File(dir, conf);
		}

		@Override
		public File[] getJars() {
			return getClassPathReader().getJars(new File(dir));
		}

		@Override
		public File[] getOutputFolders() {
			return getClassPathReader().getOutputFolders(new File(dir));
		}

		private PluginClassPathReader getClassPathReader() {
			if (reader == null) {
				reader = PluginClassPathReaderFactory.get(new File(dir));
			}
			return reader;
		}

	}

	private static class ResourcePluginInfo extends Plugin implements
			PluginInfo {
		private String baseResource;

		public ResourcePluginInfo(String resource) {
			this.baseResource = resource;
		}

		@Override
		public byte[] getDescriptorStream() throws IOException {
			return FileUtils.getContent(getPluginClassLoader()
					.getResourceAsStream(baseResource + "/plugin.xml"));
		}

		@Override
		public String toString() {
			return baseResource.toString();
		}

		@Override
		public String getRelativeSchema(String schema) throws IOException {
			return new String(FileUtils.getContent(getPluginClassLoader()
					.getResourceAsStream(getDescriptorFolder() + "/" + schema)));
		}

		private String getDescriptorFolder() {
			return baseResource;
		}

		@Override
		public File[] getJars() {
			return new File[0];
		}

		@Override
		public File[] getOutputFolders() {
			return new File[0];
		}

	}

}
