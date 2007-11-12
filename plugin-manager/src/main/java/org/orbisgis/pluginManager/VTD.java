package org.orbisgis.pluginManager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ximpleware.AutoPilot;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class VTD {

	private VTDGen gen;
	private VTDNav vn;
	private AutoPilot ap;

	public VTD(File file) throws EncodingException, EOFException,
			EntityException, ParseException, IOException {
		this(file, false);
	}

	public VTD(File file, boolean namespaceAware) throws EncodingException,
			EOFException, EntityException, ParseException, IOException {
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		byte[] content = new byte[(int) fis.getChannel().size()];
		dis.readFully(content);
		init(content, namespaceAware);
	}

	public VTD(byte[] content) throws EncodingException, EOFException,
			EntityException, ParseException {
		init(content, false);
	}

	public VTD(byte[] content, boolean nameSpaceAware)
			throws EncodingException, EOFException, EntityException,
			ParseException {
		init(content, nameSpaceAware);
	}

	private void init(byte[] content, boolean nameSpaceAware)
			throws EncodingException, EOFException, EntityException,
			ParseException {
		gen = new VTDGen();
		gen.setDoc(content);
		gen.parse(nameSpaceAware);
		vn = gen.getNav();
		ap = new AutoPilot(vn);
	}

	public int count(final String xpathExpr) throws XPathParseException {
		return evalToInt("count(" + xpathExpr + ")");
	}

	public double evalToNumber(String xpathExpr) throws XPathParseException {
		ap.selectXPath(xpathExpr);
		return ap.evalXPathToNumber();
	}

	public String[] getAttributeNames(String xpathExpr)
			throws XPathParseException, XPathEvalException, NavException {
		ap.selectXPath(xpathExpr);
		if (ap.evalXPath() != -1) {
			int namesIndex = vn.getCurrentIndex() + 1;
			ArrayList<String> ret = new ArrayList<String>();
			boolean done = false;
			while (!done) {
				if (vn.getTokenType(namesIndex) == VTDNav.TOKEN_ATTR_NAME) {
					ret.add(vn.toString(namesIndex));
				} else if (vn.getTokenType(namesIndex) == VTDNav.TOKEN_ATTR_VAL) {
				} else {
					done = true;
				}
				namesIndex++;
			}

			return ret.toArray(new String[0]);
		}

		return new String[0];
	}

	public String getNodeName(final String xpathExpr)
			throws XPathParseException, XPathEvalException, NavException {
		ap.selectXPath(xpathExpr);
		if (ap.evalXPath() != -1) {
			int namesIndex = vn.getCurrentIndex() + 1;
			boolean done = false;
			while (!done) {
				if (vn.getTokenType(namesIndex) == VTDNav.TOKEN_STARTING_TAG) {
					return vn.toNormalizedString(namesIndex);
				}
				namesIndex++;
			}
		}
		return null;
	}

	public String[] getNodeNames(final String xpathExpr)
			throws XPathParseException, XPathEvalException, NavException {
		int n = count(xpathExpr);
		if (0 == n) {
			return new String[0];
		} else {
			final List<String> ret = new ArrayList<String>(n);
			for (int i = 0; i < n; i++) {
				ret.add(getNodeName(xpathExpr));
			}
			return ret.toArray(new String[0]);
		}
	}

	public String[] getAttributeValues(String xpathExpr)
			throws XPathParseException, XPathEvalException, NavException {
		ap.selectXPath(xpathExpr);
		if (ap.evalXPath() != -1) {
			int namesIndex = vn.getCurrentIndex() + 1;
			ArrayList<String> ret = new ArrayList<String>();
			boolean done = false;
			while (!done) {
				if (vn.getTokenType(namesIndex) == VTDNav.TOKEN_ATTR_NAME) {
				} else if (vn.getTokenType(namesIndex) == VTDNav.TOKEN_ATTR_VAL) {
					ret.add(vn.toString(namesIndex));
				} else {
					done = true;
				}
				namesIndex++;
			}

			return ret.toArray(new String[0]);
		}

		return new String[0];
	}

	public String getAttribute(String xpathExpr, String attr)
			throws XPathParseException, XPathEvalException, NavException {
		ap.selectXPath(xpathExpr);
		if (ap.evalXPath() != -1) {
			int attrIndex = vn.getAttrVal(attr);
			if (attrIndex != -1) {
				return vn.toNormalizedString(attrIndex);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public int evalToInt(String xpathExpr) throws XPathParseException {
		return (int) evalToNumber(xpathExpr);
	}

	public String getContent(String xpathExpr) throws XPathParseException,
			XPathEvalException, NavException {
		ap.selectXPath(xpathExpr);
		String ret = "";
		while (ap.evalXPath() != -1) {
			long l = vn.getElementFragment();
			int offset = (int) l;
			int len = (int) (l >> 32);

			ret += new String(vn.getXML().getBytes(), offset, len);
		}

		return ret;
	}

	public String evalToString(String xpathExpr) throws XPathParseException {
		ap.selectXPath(xpathExpr);
		return ap.evalXPathToString();
	}

	public void declareXPathNameSpace(String prefix, String nsUrl) {
		ap.declareXPathNameSpace(prefix, nsUrl);

	}
}
