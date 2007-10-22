package org.orbisgis.pluginManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

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

	public static void main(String[] args) throws Exception {
		if (args.length > 1) {
			System.err
					.println("Usage: java org.orbisgis.pluginManager.Main [plugin-list.xml]");
		}

		File pluginList;
		if (args.length == 0) {
			pluginList = new File("./plugin-list.xml");
		} else {
			pluginList = new File(args[0]);
		}

		ArrayList<String> pluginDirs = getPluginsDirs(pluginList);

		commonClassLoader = new CommonClassLoader();

		ArrayList<Plugin> plugins = createExtensionRegistry(pluginDirs);

		commonClassLoader.finished();

		PluginManager.createPluginManager(plugins);

		PluginManager.start();
	}

	private static ArrayList<Plugin> createExtensionRegistry(
			ArrayList<String> pluginDirs) throws Exception {
		ArrayList<Plugin> plugins = new ArrayList<Plugin>();
		HashMap<String, ExtensionPoint> extensionPoints = new HashMap<String, ExtensionPoint>();
		ArrayList<Extension> extensions = new ArrayList<Extension>();
		for (String pluginDir : pluginDirs) {
			File pluginXML = new File(pluginDir, "plugin.xml");
			if (pluginXML.exists()) {
				VTD vtd = new VTD(pluginXML);
				int n = vtd.evalToInt("count(/plugin/extension-point)");
				for (int i = 0; i < n; i++) {
					String schema = vtd.getAttribute("/plugin/extension-point["
							+ (i + 1) + "]", "schema");
					File schemaFile = new File(pluginDir, schema);
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
							pluginClassLoader);
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
			}
		}

		resolveExtensions(extensions, extensionPoints);

		RegistryFactory.createExtensionRegistry(extensions);

		return plugins;
	}

	private static void updateCommonClassLoader(String pluginDir,
			String[] isolatedJars) {
		File dir = new File(pluginDir);
		PluginClassPathReader reader = PluginClassPathReaderFactory.get(dir);
		commonClassLoader.addJars(reader.getJars(dir));
		commonClassLoader.addOutputFolders(reader.getOutputFolders(dir));
	}

	private static CommonClassLoader getIsolatedClassLoader(
			String[] isolatedJars) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	private static void resolveExtensions(ArrayList<Extension> extensions,
			HashMap<String, ExtensionPoint> extensionPoints) throws Exception {
		for (Extension extension : extensions) {
			ExtensionPoint extensionPoint = extensionPoints.get(extension
					.getPoint());
			if (extensionPoint == null) {
				throw new Exception("Extension point " + extension.getPoint()
						+ " not found. In extension " + extension.getId());
			}
			validateXML(extensionPoint.getSchema(), extension);
			extensionPoint.addExtension(extension);
		}
	}

	private static void validateXML(File schemaFile, final Extension extension)
			throws Exception {
		SAXParserFactory spfactory = SAXParserFactory.newInstance();
		spfactory.setNamespaceAware(false);
		spfactory.setValidating(true);
		SAXParser saxparser = spfactory.newSAXParser();

		saxparser.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
		saxparser.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaSource",
				schemaFile);

		// write your handler for processing events and handling error
		DefaultHandler handler = new DefaultHandler() {

			@Override
			public void warning(SAXParseException e) throws SAXException {
				fail(e);
			}

			private void fail(SAXParseException e) {
				throw new RuntimeException("\n extension id: "
						+ extension.getId() + "\n" + extension.getXml(), e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				fail(e);
			}

			@Override
			public void error(SAXParseException e) throws SAXException {
				fail(e);
			}

		};

		// parse the XML and report events and errors (if any) to the handler
		saxparser.parse(
				new ByteArrayInputStream(extension.getXml().getBytes()),
				handler);
	}

	private static ArrayList<String> getPluginsDirs(File pluginList)
			throws FileNotFoundException, IOException, EncodingException,
			EOFException, EntityException, ParseException, XPathParseException,
			XPathEvalException, NavException {
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
}
