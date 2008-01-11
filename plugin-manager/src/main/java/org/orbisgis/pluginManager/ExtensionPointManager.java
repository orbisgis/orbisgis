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

import java.util.ArrayList;

public class ExtensionPointManager<T> {

	private String id;

	public ExtensionPointManager(String id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<T> getInstancesFrom(String tag, String attribute) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(id);
		ArrayList<T> instances = new ArrayList<T>();
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();
			if (c.getAttribute(tag, attribute) != null) {
				T nr = (T) c.instantiateFromAttribute(tag, attribute);
				instances.add(nr);
			}
		}

		return instances;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ItemAttributes<T>> getItemAttributes(String xpath) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(id);
		ArrayList<ItemAttributes<T>> instances = new ArrayList<ItemAttributes<T>>();
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();
			String[] attributeNames = c.getAttributeNames(xpath);
			if (attributeNames.length > 0) {
				ItemAttributes<T> ia = new ItemAttributes<T>(c, xpath,
						attributeNames, c.getAttributeValues(xpath));
				instances.add(ia);
			}
		}

		return instances;
	}

	@SuppressWarnings("unchecked")
	public T instantiateFrom(String condition, String classAttribute) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(id);
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();
			if (c.getAttribute(condition, classAttribute) != null) {
				T action = (T) c.instantiateFromAttribute(condition,
						classAttribute);
				return action;
			}
		}

		return null;
	}
}
