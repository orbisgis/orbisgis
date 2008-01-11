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
package org.orbisgis.geoview.renderer.style.sld;

import java.util.ArrayList;
import java.util.List;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

/**
 *
 *
 * @author bocher
 *
 */
public class FeatureTypeStyle {

	private VTD vtd;

	private String rootXpathQuery;

	private String name;

	private String title;

	public String childXpathQuery = "/sld:Rule";

	private List<Rule> rules;

	public FeatureTypeStyle(VTD vtd, String rootXpathQuery)
			throws XPathParseException, XPathEvalException, NavException {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
		rules = new ArrayList<Rule>();
		int n = getRuleCount();

		for (int i = 0; i < n; i++) {
			rules.add(new Rule(vtd, rootXpathQuery + childXpathQuery + "["
					+ (i + 1) + "]"));
		}
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

	public String getFeatureTypeName() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:FeatureTypeName");
	}

	public String getSemanticTypeIdentifier() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:SemanticTypeIdentifier");
	}

	public int getRuleCount() throws XPathParseException {
		return vtd.evalToInt("count(" + rootXpathQuery + childXpathQuery + ")");
	}

	public List<Rule> getRules() {
		return rules;
	}
}