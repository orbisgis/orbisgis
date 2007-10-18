package org.orbisgis.pluginManager;

import java.io.File;
import java.util.ArrayList;

public class ExtensionPoint {
	private File schema;
	private String id;
	private ArrayList<Extension> extensions;

	public ExtensionPoint(File schema, String id) {
		super();
		this.schema = schema;
		this.id = id;
	}

	protected File getSchema() {
		return schema;
	}

	protected void setSchema(File schema) {
		this.schema = schema;
	}

	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

	public void addExtension(Extension extension) {
		extensions.add(extension);
	}

}
