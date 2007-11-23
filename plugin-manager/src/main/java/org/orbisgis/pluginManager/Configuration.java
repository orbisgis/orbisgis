package org.orbisgis.pluginManager;

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

}
