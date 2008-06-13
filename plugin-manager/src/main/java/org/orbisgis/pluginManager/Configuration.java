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

import java.util.HashMap;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class Configuration {

	private VTD vtd;
	private ClassLoader loader;
	private String xml;

	public Configuration(String xml, ClassLoader loader)
			throws EncodingException, EOFException, EntityException,
			ParseException {
		this.xml = xml;
		vtd = new VTD(xml.getBytes());
		this.loader = loader;
	}

	public Object instantiateFromAttribute(String xpath, String attribute) {
		String className;
		try {
			className = vtd.getAttribute(xpath, attribute);
			return loader.loadClass(className).newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("bug!", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("bug!", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("bug!", e);
		} catch (XPathEvalException e) {
			throw new RuntimeException("bug!", e);
		} catch (NavException e) {
			throw new RuntimeException("bug!", e);
		} catch (XPathParseException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getConfigurationXml() {
		return xml;
	}

	public String getAttribute(String baseXPath, String attributeName) {
		try {
			return vtd.getAttribute(baseXPath, attributeName);
		} catch (XPathParseException e) {
			throw new RuntimeException(e);
		} catch (XPathEvalException e) {
			throw new RuntimeException(e);
		} catch (NavException e) {
			throw new RuntimeException(e);
		}
	}

	public int evalInt(String xpath) {
		try {
			return vtd.evalToInt(xpath);
		} catch (XPathParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String[] getAttributeNames(String xpath) {
		try {
			return vtd.getAttributeNames(xpath);
		} catch (XPathParseException e) {
			throw new RuntimeException(e);
		} catch (XPathEvalException e) {
			throw new RuntimeException(e);
		} catch (NavException e) {
			throw new RuntimeException(e);
		}
	}

	public String[] getAttributeValues(String xpath) {
		try {
			return vtd.getAttributeValues(xpath);
		} catch (XPathParseException e) {
			throw new RuntimeException(e);
		} catch (XPathEvalException e) {
			throw new RuntimeException(e);
		} catch (NavException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean getBooleanAttribute(String baseXPath, String attributeName) {
		String att = getAttribute(baseXPath, attributeName);
		if (att == null) {
			return false;
		} else {
			return Boolean.parseBoolean(att);
		}
	}

	public HashMap<String, String> getAttributes(String xpath) {
		HashMap<String, String> ret = new HashMap<String, String>();
		try {
			String[] attributes = vtd.getAttributeNames(xpath);
			for (String attribute : attributes) {
				String attrValue;
				attrValue = vtd.getAttribute(xpath, attribute);
				ret.put(attribute, attrValue);
			}
		} catch (XPathParseException e) {
			throw new RuntimeException(e);
		} catch (XPathEvalException e) {
			throw new RuntimeException(e);
		} catch (NavException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

}
