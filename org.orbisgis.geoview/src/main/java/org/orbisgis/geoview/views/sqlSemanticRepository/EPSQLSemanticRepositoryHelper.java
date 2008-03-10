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
package org.orbisgis.geoview.views.sqlSemanticRepository;

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.Category;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.SqlInstruction;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.SqlScript;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.RegistryFactory;
import org.xml.sax.SAXException;

public class EPSQLSemanticRepositoryHelper {

	public static Category install() throws JAXBException, SAXException {
		final IExtensionRegistry er = RegistryFactory.getRegistry();
		final Extension[] extensions = er
				.getExtensions("org.orbisgis.geoview.SQLRepository");
		final Category category = new Category();
		category.setId("root");
		for (Extension extension : extensions) {
			final String resourcePath = extension.getConfiguration()
					.getAttribute("sql", "resource-path");
			// active part : populate here the menu (TreeModel...)
			try {
				Category subCategory = getSubCategory(EPSQLSemanticRepositoryHelper.class
						.getResource(resourcePath));
				performCheck(resourcePath, subCategory);
				category.getCategoryOrSqlScriptOrSqlInstruction().add(
						subCategory);
			} catch (JAXBException e) {
				PluginManager.error("Cannot add the resource "
						+ "to the sql repostitory: " + resourcePath, e);
			}
		}
		return category;
	}

	private static void performCheck(String resourcePath, Category subCategory) {
		try {
			check(subCategory);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Problem including: " + resourcePath
					+ ": " + e.getMessage());
		} catch (InstantiationException e) {
			throw new RuntimeException("Problem including: " + resourcePath
					+ ": " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Problem including: " + resourcePath
					+ ": " + e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Problem including: " + resourcePath
					+ ": " + e.getMessage(), e);
		}
	}

	private static void check(Category category) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		List<Object> list = category.getCategoryOrSqlScriptOrSqlInstruction();
		for (Object object : list) {
			if (object instanceof Category) {
				check((Category) object);
			} else if (object instanceof SqlScript) {
				SqlScript script = (SqlScript) object;
				String resource = script.getResource();
				if (EPSQLSemanticRepositoryHelper.class.getResource(resource) == null) {
					throw new IllegalArgumentException("Cannot find: "
							+ resource);
				}
			} else if (object instanceof SqlInstruction) {
				SqlInstruction instruction = (SqlInstruction) object;
				String className = instruction.getClazz();
				final Object newInstance = Class.forName(className)
						.newInstance();
				if (!(newInstance instanceof Function)
						&& !(newInstance instanceof CustomQuery)) {
					throw new RuntimeException("The specified class is "
							+ "not function nor custom query: " + className);
				}
			} else {
				throw new RuntimeException("bug: unexpected type");
			}
		}
	}

	private static Category getSubCategory(final URL xmlFileUrl)
			throws JAXBException, SAXException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(
				"org.orbisgis.geoview.views.sqlSemanticRepository.persistence",
				EPSQLSemanticRepositoryHelper.class.getClassLoader())
				.createUnmarshaller();
		SchemaFactory sf = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		URL schemaURL = EPSQLSemanticRepositoryHelper.class
				.getResource("/org/orbisgis/geoview"
						+ "/sqlRepository/sqlSemanticRepository.xsd");
		unmarshaller.setSchema(sf.newSchema(schemaURL));
		return (Category) unmarshaller.unmarshal(xmlFileUrl);
	}
}