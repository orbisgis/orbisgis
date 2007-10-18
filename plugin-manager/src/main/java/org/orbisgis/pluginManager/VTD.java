package org.orbisgis.pluginManager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		byte[] content = new byte[(int) fis.getChannel().size()];
		dis.readFully(content);
		init(content);
	}

	public VTD(byte[] content) throws EncodingException, EOFException,
			EntityException, ParseException {
		init(content);
	}

	private void init(byte[] content) throws EncodingException, EOFException,
			EntityException, ParseException {
		gen = new VTDGen();
		gen.setDoc(content);
		gen.parse(false);
		vn = gen.getNav();
		ap = new AutoPilot(vn);
	}

	public double evalToNumber(String xpathExpr) throws XPathParseException {
		ap.selectXPath(xpathExpr);
		return ap.evalXPathToNumber();
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
}
