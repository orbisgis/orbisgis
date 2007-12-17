package org.orbisgis.geoview.views.sqlSemanticRepository;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.Menu;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class EPSQLSemanticRepositoryHelper {
	public static Menu install() {
		final IExtensionRegistry er = RegistryFactory.getRegistry();
		final Extension[] extensions = er
				.getExtensions("org.orbisgis.geoview.SQLSemanticRepository");
		final Menu menu = new Menu();
		for (Extension extension : extensions) {
			final String resourcePath = extension.getConfiguration()
					.getAttribute("sql", "resource-path");
			// active part : populate here the menu (TreeModel...)
			try {
				menu.getMenuOrMenuItem().add(
						getSubMenu(EPSQLSemanticRepositoryHelper.class
								.getResource(resourcePath)));
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return menu;
	}

	private static Menu getSubMenu(final URL xmlFileUrl) throws JAXBException {
		return (Menu) JAXBContext
				.newInstance(
						"org.orbisgis.geoview.views.sqlSemanticRepository.persistence",
						EPSQLSemanticRepositoryHelper.class.getClassLoader())
				.createUnmarshaller().unmarshal(xmlFileUrl);
	}
}