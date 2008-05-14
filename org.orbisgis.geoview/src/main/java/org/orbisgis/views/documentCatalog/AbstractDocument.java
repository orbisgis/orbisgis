package org.orbisgis.views.documentCatalog;

import javax.swing.Icon;

public abstract class AbstractDocument implements IDocument {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Icon getIcon() {
		return null;
	}
}
