package org.orbisgis.geoview.fromXmlToSQLTree;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.basic.persistence.Menu;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class EPFromXmlToSqlTreeHelper {
	public static Menu install() {
		final IExtensionRegistry er = RegistryFactory.getRegistry();
		final Extension[] extensions = er
				.getExtensions("org.orbisgis.geoview.FromXmlToSqlTree");
		final Menu menu = new Menu();
		for (Extension extension : extensions) {
			final String path = extension.getConfiguration().getAttribute(
					"sql", "resource-path");
			// active part : populate here the menu (TreeModel...)
			try {
				menu.getMenuOrMenuItem().add(
						getSubMenu(new File(path).toURI().toURL()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return menu;
	}

	private static Menu getSubMenu(final URL xmlFileUrl) throws JAXBException {
		return (Menu) JAXBContext.newInstance(
				"org.orbisgis.geoview.basic.persistence",
				EPFromXmlToSqlTreeHelper.class.getClassLoader())
				.createUnmarshaller().unmarshal(xmlFileUrl);
	}
}