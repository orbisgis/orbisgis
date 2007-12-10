package org.orbisgis.geoview.renderer.style.sld;

import java.util.ArrayList;

import org.gdms.sql.evaluator.And;
import org.gdms.sql.evaluator.Equals;
import org.gdms.sql.evaluator.GreaterThanOrEqual;
import org.gdms.sql.evaluator.LessThan;
import org.gdms.sql.evaluator.Literal;
import org.gdms.sql.evaluator.Node;
import org.gdms.sql.evaluator.Or;
import org.gdms.sql.evaluator.Property;
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

	public Node getExpression() throws XPathParseException, XPathEvalException,
			NavException {
		return getExpression(rootXpathQuery+"/child::*[1]");
	}

	private Node getExpression(String baseXpath) throws XPathParseException,
			XPathEvalException, NavException {
		
		ArrayList<Node> childs = new ArrayList<Node>();
		
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
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyIsEqualTo")) {
			return new Equals(childs.get(0), childs.get(1));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:PropertyName")) {
			return new Property(vtd.evalToString(baseXpath));
		
		}
		
		else if (vtd.getNodeName(baseXpath).equalsIgnoreCase("ogc:Literal")) {
			return new Literal(vtd.evalToString(baseXpath));
		
		}		
		
		
		return null;
	}

}
