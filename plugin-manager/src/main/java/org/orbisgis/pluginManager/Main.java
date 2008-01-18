/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.pluginManager;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.orbisgis.XMLUtils;

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
	private static File pluginList;
	private static boolean clean = false;

	public static void main(String[] args) throws Exception {
		parseArguments(args);

		PropertyConfigurator.configure(Main.class
				.getResource("log4j.properties"));

		PatternLayout l = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
		RollingFileAppender fa = new RollingFileAppender(l, PluginManager
				.getLogFile());
		fa.setMaxFileSize("256KB");
		Logger.getRootLogger().addAppender(fa);

		Splash splash = new Splash();
		splash.setVisible(true);

		try {
			ArrayList<String> pluginDirs = getPluginsDirs(pluginList);

			Toolkit.getDefaultToolkit().getSystemEventQueue().push(
					new OrbisgisEventQueue());

			commonClassLoader = new CommonClassLoader();

			ArrayList<Plugin> plugins = createExtensionRegistry(pluginDirs);

			commonClassLoader.finished();

			PluginManager.createPluginManager(plugins);

			PluginManager.getWorkspace().init(clean);

			PluginManager.start();

		} catch (Exception e) {
			splash.setVisible(false);
			splash.dispose();
			throw e;
		}

		splash.setVisible(false);
		splash.dispose();
	}

	private static void parseArguments(String[] args) throws IOException {
		pluginList = new File("./plugin-list.xml");
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-document")) {
				doc = true;
			} else if (args[i].equals("-p")) {
				pluginList = new File(args[i + 1]);
				i++;
			} else if (args[i].equals("-w")) {
				PluginManager.getWorkspace().setWorkspaceFolder(args[i + 1]);
				i++;
			} else if (args[i].equals("-clean")) {
				clean = true;
			} else {
				System.err
						.println("usage Usage: java org.orbisgis.pluginManager.Main [-clean] [-p plugin-list.xml] [-w workspace-dir]");
			}
		}
	}

	private static ArrayList<Plugin> createExtensionRegistry(
			ArrayList<String> pluginDirs) throws Exception {
		ArrayList<Plugin> plugins = new ArrayList<Plugin>();
		HashMap<String, ExtensionPoint> extensionPoints = new HashMap<String, ExtensionPoint>();
		ArrayList<Extension> extensions = new ArrayList<Extension>();
		for (String pluginDir : pluginDirs) {
			logger.debug("Reading plugin.xml of " + pluginDir);
			File pluginXML = new File(pluginDir, "plugin.xml");
			if (pluginXML.exists()) {
				VTD vtd = new VTD(pluginXML);

				int n = vtd.evalToInt("count(/plugin/extension-point)");
				for (int i = 0; i < n; i++) {
					String schema = vtd.getAttribute("/plugin/extension-point["
							+ (i + 1) + "]", "schema");
					File schemaFile = getSchemaFile(pluginDirs, pluginDir,
							schema);
					if (!schemaFile.exists()) {
						throw new IOException(schemaFile.getAbsolutePath()
								+ " not found");
					}
					String id = vtd.getAttribute("/plugin/extension-point["
							+ (i + 1) + "]", "id");

					ExtensionPoint ep = new ExtensionPoint(schemaFile, id);
					extensionPoints.put(id, ep);
				}

				String[] isolatedJars = new String[vtd
						.evalToInt("count(/plugin/isolated)")];
				for (int i = 0; i < isolatedJars.length; i++) {
					isolatedJars[i] = vtd.getContent("/plugin/isolated["
							+ (i + 1) + "]");
				}
				CommonClassLoader pluginClassLoader;
				if (isolatedJars.length == 0) {
					pluginClassLoader = commonClassLoader;
				} else {
					pluginClassLoader = getIsolatedClassLoader(isolatedJars);
				}
				updateCommonClassLoader(pluginDir, isolatedJars);

				n = vtd.evalToInt("count(/plugin/extension)");
				for (int i = 0; i < n; i++) {
					String point = vtd.getAttribute("/plugin/extension["
							+ (i + 1) + "]", "point");
					String xml = vtd.getContent("/plugin/extension[" + (i + 1)
							+ "]");
					String id = vtd.getAttribute("/plugin/extension[" + (i + 1)
							+ "]", "id");

					Extension e = new Extension(xml, point, id,
							pluginClassLoader, pluginDir);
					extensions.add(e);
				}

				String activatorClassName = vtd
						.evalToString("/plugin/activator/@class");
				if (activatorClassName.equals("")) {
					activatorClassName = null;
				}

				Plugin plugin = new Plugin(activatorClassName, new File(
						pluginDir), pluginClassLoader);
				plugins.add(plugin);
			} else {
				throw new RuntimeException("Plugin not found: " + pluginDir);
			}
		}

		resolveExtensions(extensions, extensionPoints);

		RegistryFactory.createExtensionRegistry(extensions);

		if (doc) {
			generateDocumentation(extensionPoints, extensions);
		}

		return plugins;
	}

	private static void generateDocumentation(
			HashMap<String, ExtensionPoint> extensionPoints,
			ArrayList<Extension> extensions) throws Exception {
		Iterator<String> epIterator = extensionPoints.keySet().iterator();
		while (epIterator.hasNext()) {
			String epId = epIterator.next();
			ExtensionPoint ep = extensionPoints.get(epId);
			File schema = ep.getSchema();
			String schemaContent = getContents(schema);
			InputStream templateStream = Main.class
					.getResourceAsStream("/schema-template.xml");
			String template = getContents(templateStream);
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
			new File("docs").mkdirs();
			trans.transform(xmlSource, new StreamResult("docs/" + epId
					+ ".html"));
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

	private static String getContents(File schema) throws IOException {
		FileInputStream fis = new FileInputStream(schema);
		return getContents(fis);
	}

	private static String getContents(InputStream fis) throws IOException {
		DataInputStream dis = new DataInputStream(fis);
		byte[] content = new byte[dis.available()];
		dis.readFully(content);
		dis.close();
		return new String(content);
	}

	private static File getSchemaFile(ArrayList<String> pluginDirs,
			String pluginDir, String schema) {
		int ref = schema.indexOf("${");
		if (ref != -1) {
			int beggining = ref;
			int end = schema.indexOf("}");
			String referencedPlugin = schema.substring(beggining + 2, end);
			for (String dir : pluginDirs) {
				File dirFile = new File(dir);
				String name = dirFile.getName();
				if (name.equals(referencedPlugin)) {
					String path = dirFile.getAbsolutePath() + File.separator;
					path = path.replaceAll("\\Q\\\\E", "/");
					schema = schema.replaceAll("\\Q"
							+ schema.substring(beggining, end + 1) + "\\E",
							path);
					return new File(schema);
				}
			}

			throw new RuntimeException("Cannot find referenced project: "
					+ referencedPlugin);
		} else {
			return new File(pluginDir, schema);
		}
	}

	private static void updateCommonClassLoader(String pluginDir,
			String[] isolatedJars) throws IOException {
		File dir = new File(pluginDir);
		PluginClassPathReader reader = PluginClassPathReaderFactory.get(dir);
		commonClassLoader.addJars(reader.getJars(dir));
		commonClassLoader.addOutputFolders(reader.getOutputFolders(dir));
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
						+ " in " + extension.getPluginDir());
			}
			String error = XMLUtils.validateXML(extensionPoint.getSchema(),
					extension.getXml());
			if (error != null) {
				throw new Exception(
						"The content of the extension does not match the schema. Extension: "
								+ extension.getId() + " in plugin "
								+ extension.getPluginDir() + ": " + error);
			}
			extensionPoint.addExtension(extension);
		}
	}

	private static ArrayList<String> getPluginsDirs(File pluginList)
			throws FileNotFoundException, IOException, EncodingException,
			EOFException, EntityException, ParseException, XPathParseException,
			XPathEvalException, NavException {
		logger.debug("Reading plugin list");
		ArrayList<String> pluginDirs = new ArrayList<String>();
		if (pluginList.exists()) {
			VTD vtd = new VTD(pluginList);
			int n = vtd.evalToInt("count(/plugins/plugin)");
			for (int i = 0; i < n; i++) {
				String pluginDir = vtd.getAttribute("/plugins/plugin["
						+ (i + 1) + "]", "dir");
				logger.debug("Plugin: " + pluginDir);
				pluginDirs.add(pluginDir);
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
					pluginDirs.add(pluginDir.getAbsolutePath());
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
		private HashSet<Class<? extends AWTEvent>> meaningfulEvents = new HashSet<Class<? extends AWTEvent>>();

		public OrbisgisEventQueue() {
			meaningfulEvents.add(ActionEvent.class);
			meaningfulEvents.add(ComponentEvent.class);
			meaningfulEvents.add(WindowEvent.class);
			meaningfulEvents.add(KeyEvent.class);
			meaningfulEvents.add(InputMethodEvent.class);
		}

		@Override
		protected void dispatchEvent(AWTEvent event) {
			try {
				super.dispatchEvent(event);
				if (meaningfulEvents.contains(event.getClass())) {
					PluginManager.fireEvent();
				}
			} catch (Exception e) {
				PluginManager.error(e.getMessage(), e);
			} catch (OutOfMemoryError e) {
				PluginManager.error(
						"Out of memory error. It's "
								+ "strongly recomended to "
								+ "restart the application", e);
			} catch (Throwable e) {
				PluginManager.error(e.getMessage(), e);
			}
		}
	}

}
