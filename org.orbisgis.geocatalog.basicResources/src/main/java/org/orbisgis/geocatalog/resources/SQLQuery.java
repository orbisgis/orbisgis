package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.orbisgis.core.resourceTree.BasicResource;
import org.orbisgis.core.resourceTree.IResource;

public class SQLQuery extends BasicResource {

	private String query = null;

	private final Icon icon = new ImageIcon(getClass().getResource(
			"sqlquery.png"));

	public SQLQuery(String name, String query) {
		super(name);
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Icon getIcon(boolean isExpanded) {
		return icon;
	}

	public void addChild(IResource child, int index) {
		// A SQL Query cannot have children
	}

}
