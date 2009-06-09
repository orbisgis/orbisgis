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
 */
package org.orbisgis.pluginManager;

import java.io.InputStream;

public class ItemAttributes<T> {

	private String[] attributes;
	private String[] values;
	private Configuration configuration;
	private String xpath;

	public ItemAttributes(Configuration c, String xpath, String[] attributes,
			String[] values) {
		this.attributes = attributes;
		this.values = values;
		this.configuration = c;
		this.xpath = xpath;
	}

	public String getAttribute(String name) {
		int index = getAttrIndex(name);

		if (index != -1) {
			return values[index];
		} else {
			return null;
		}
	}

	public boolean exists(String attributeName) {
		return getAttrIndex(attributeName) != -1;
	}

	@SuppressWarnings("unchecked")
	public T getInstance(String attributeName) {
		return (T) configuration.instantiateFromAttribute(xpath, attributeName);
	}

	private int getAttrIndex(String name) {
		int index = -1;
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].equals(name)) {
				index = i;
				break;
			}
		}
		return index;
	}
	public InputStream getResourceAsStream(String resourceName) {
		return configuration.getResourceAsStream(xpath, resourceName);
	}
}
