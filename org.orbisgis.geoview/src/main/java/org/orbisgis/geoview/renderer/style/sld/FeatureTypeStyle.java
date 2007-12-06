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