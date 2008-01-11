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

import org.gdms.sql.evaluator.And;
import org.gdms.sql.evaluator.Equals;
import org.gdms.sql.evaluator.GreaterThan;
import org.gdms.sql.evaluator.GreaterThanOrEqual;
import org.gdms.sql.evaluator.LessThan;
import org.gdms.sql.evaluator.LessThanOrEqual;
import org.gdms.sql.evaluator.Literal;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Or;
import org.gdms.sql.evaluator.Field;
import org.orbisgis.pluginManager.VTD;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class Filter {

	private VTD vtd;

	private String rootXpathQuery;

	public Filter(VTD vtd, String rootXpathQuery) {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}

	public String toString() {

		try {

			return vtd.getContent(rootXpathQuery);
		} catch (XPathParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathEvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NavException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Expression getExpression() throws XPathParseException, XPathEvalException,
			NavException {
		return getExpression(rootXpathQuery+"/child::*[1]");
	}

	private Expression getExpression(String baseXpath) throws XPathParseException,
			XPathEvalException, NavException {
		
		ArrayList<Expression> childs = new ArrayList<Expression>();
		
		int childsCount = vtd.evalToInt("count("+ baseXpath + "/*)");
		for (int i = 0; i < childsCount; i++) {
			
			childs.add(getExpression(baseXpath + "/child::*["+(i+1)+"]"));
			
		}
		
		if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:And")){
			
			return new And(childs.get(0), childs.get(1));
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:Or")) {
			return new Or(childs.get(0), childs.get(1));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyIsGreaterThanOrEqualTo")) {
			return new GreaterThanOrEqual(childs.get(0), childs.get(1));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyIsLessThan")) {
			return new LessThan(childs.get(0), childs.get(1));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyIsLessThanOrEqualTo")) {
			return new LessThanOrEqual(childs.get(0), childs.get(1));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyIsGreaterThan")) {
			return new GreaterThan(childs.get(0), childs.get(1));
		
		}
		
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyIsEqualTo")) {
			return new Equals(childs.get(0), childs.get(1));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyName")) {
			return new Field(vtd.evalToString(baseXpath));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:Literal")) {
			return new Literal(vtd.evalToString(baseXpath));
		
		}	
		
		return null;
	}

}
