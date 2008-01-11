/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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