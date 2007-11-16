package org.orbisgis.pluginManager;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.ParseException;

public class Extension {
	private String xml;
	private String point;
	private ClassLoader loader;
	private String id;
	private String pluginDir;

	public Extension(String xml, String point, String id, ClassLoader loader, String pluginDir) {
		super();
		this.xml = xml;
		this.point = point;
		this.loader = loader;
		this.id = id;
		this.pluginDir = pluginDir;
	}

	protected String getXml() {
		return xml;
	}

	protected void setXml(String xml) {
		this.xml = xml;
	}

	protected String getPoint() {
		return point;
	}

	protected void setPoint(String point) {
		this.point = point;
	}

	public Configuration getConfiguration() {
		try {
			return new Configuration(xml, loader);
		} catch (EncodingException e) {
		} catch (EOFException e) {
		} catch (EntityException e) {
		} catch (ParseException e) {
		}
		throw new RuntimeException("The content has already "
				+ "been processed, but it fails... This is a bug");
	}

	public String getId() {
		return this.id;
	}

	public ClassLoader getLoader() {
		return loader;
	}

	public String getPluginDir() {
		return pluginDir;
	}

}
