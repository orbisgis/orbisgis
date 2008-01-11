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
package org.orbisgis.geoview.renderer.style.sld;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.STRING;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class Rule {

	private VTD vtd;

	private String rootXpathQuery;

	public String childXpathQuery;

	public Rule(VTD vtd, String rootXpathQuery) throws XPathParseException,
			XPathEvalException, NavException {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
		childXpathQuery = rootXpathQuery + "/sld:PointSymbolizer|"
				+ rootXpathQuery + "/sld:LineSymbolizer|" + rootXpathQuery
				+ "/sld:PolygonSymbolizer";

	}

	public String getName() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:Name");
	}

	public String getTitle() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:Title");
	}

	public String getAbstract() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:Abstract");
	}

	public Double getMaxScaleDenominator() throws XPathParseException {
		return vtd.evalToNumber(rootXpathQuery + "/sld:MaxScaleDenominator");
	}

	public Symbolizer getSymbolizer() throws XPathParseException,
			XPathEvalException, NavException {

		String nodeName = vtd.getNodeName(childXpathQuery);

		System.out.println(nodeName);

		if (nodeName.equalsIgnoreCase("sld:PointSymbolizer")) {
			return new PointSymbolizer(vtd, rootXpathQuery
					+ "/sld:PointSymbolizer");
		} else if (nodeName.equalsIgnoreCase("sld:LineSymbolizer")) {

			return new LineSymbolizer(vtd, rootXpathQuery
					+ "/sld:LineSymbolizer");
		}

		else if (nodeName.equalsIgnoreCase("sld:PolygonSymbolizer")) {

			return new PolygonSymbolizer(vtd, rootXpathQuery
					+ "/sld:PolygonSymbolizer");
		} else {

		}

		return null;

	}

	public int getFilterCount() throws XPathParseException,
	XPathEvalException, NavException {
		return vtd.evalToInt("count("+rootXpathQuery + "/ogc:Filter)");
	}

	public Filter getFilter(int n) throws XPathParseException,
			XPathEvalException, NavException {

		/**
		 * todo add expression
		 */
		return new Filter(vtd, rootXpathQuery + "/ogc:Filter[" + (n + 1) + "]");

	}
}