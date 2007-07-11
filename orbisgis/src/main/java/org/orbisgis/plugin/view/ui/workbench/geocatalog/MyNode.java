package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Class MyNode
 * 
 * Each node of the Catalog tree is an instance of MyNode. It
 * contains the type of the node (folder, datasource, sldfile,...)
 * and links to children and parent.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class MyNode {

	private Vector<MyNode> children = null;

	private MyNode father = null;

	private String name = null;

	private int type = 0;

	private File file = null;

	private String driver = null;

	private String query = null;

	public final static int folder = 0;

	public final static int datasource = 1;

	public final static int sldfile = 2;

	public final static int sqlquery = 3;

	public final static int sldlink = 4;

	public final static int raster = 5;

	public MyNode(String name, int type) {
		this.name = name;
		this.type = type;
		children = new Vector<MyNode>();
	}

	public MyNode(String name, int type, String driver, File file) {
		this.name = name;
		this.type = type;
		children = new Vector<MyNode>();
		this.driver = driver;
		this.file = file;
	}

	public MyNode(String name, int type, String query) {
		this.name = name;
		this.type = type;
		children = new Vector<MyNode>();
		this.query = query;
	}

	/**
	 * Changes the name of a node
	 * 
	 * @param newName
	 *            The new name for the node
	 * @return The old name
	 */
	public String setName(String newName) {
		String oldName = this.name;
		this.name = newName;
		return oldName;
	}
	
	public String setQuery(String newQuery) {
		String oldQuery = this.query;
		this.query = newQuery;
		return oldQuery;
	}

	/** Retrieves the name of the node */
	public String getName() {
		return name;
	}

	/** Retrieves the name of the node */
	public String toString() {
		return name;
	}

	/** Retrieves the type of a node (folder, datasource, sld...) */
	public int getType() {
		return type;
	}

	/**
	 * Retrieves the name of the driver. This is mostly used to make a
	 * distinction between all kind of datasources and choose some beautiful
	 * icons
	 * 
	 */
	public String getDriverName() {
		return driver;
	}

	/**
	 * Retrieves the file associated with the node. It is used for sld files but
	 * not for datasources
	 * 
	 */
	public File getFile() {
		return file;
	}

	/** Copy the node this and return a node of the type sldlink */
	public MyNode createLink() {
		MyNode node = new MyNode(this.name, MyNode.sldlink, null, this.file);
		return node;
	}

	/** Allow to retrieve the SQL query of the node */
	public String getQuery() {
		return query;
	}

	public void setParent(MyNode father) {
		this.father = father;
	}

	public MyNode getParent() {
		return father;
	}

	public int getChildCount() {
		return children.size();
	}

	public MyNode getChildAt(int i) {
		return (MyNode) children.elementAt(i);
	}

	public int getIndexOfChild(MyNode kid) {
		return children.indexOf(kid);
	}
	
	public boolean haveChildren() {
		return !children.isEmpty();
	}

	/** Add a child to this at last position. */
	public void add(MyNode child) {
		add(child, getChildCount());
	}
	
	/** Add a child to this at index position. */
	public void add(MyNode child, int index) {
		children.add(index, child);
		child.setParent(this);
	}

	/** Removes a child */
	public void remove(MyNode child) {
		children.remove(child);
	}
	
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	/**
	 * Retrieves the nodes to root First element is root, last element is this
	 * 
	 */
	public MyNode[] getPath() {
		ArrayList<MyNode> path = new ArrayList<MyNode>();
		MyNode current = this;
		while (current != null) {
			path.add(current);
			current = current.getParent();
		}

		// Now we must reverse the order
		ArrayList<MyNode> path2 = new ArrayList<MyNode>();
		int l = path.size();
		for (int i = 0; i < l; i++) {
			path2.add(i, path.get(l - i - 1));
		}

		return path2.toArray(new MyNode[0]);
	}

	public ArrayList<MyNode> depthChildList() {
		ArrayList<MyNode> childList = new ArrayList<MyNode>();

		for (MyNode child : children) {
			if (child.isLeaf()) {
				childList.add(child);
			} else {
				ArrayList<MyNode> subChildList = child.depthChildList();
				for (MyNode subChild : subChildList) {
					childList.add(subChild);
				}
				childList.add(child);
			}
		}

		return childList;
	}
	
	public boolean equals(MyNode node) {
		boolean ok = false;
		String name = node.getName();
		String driver = node.getDriverName();
		
		ok = name.equalsIgnoreCase(this.getName());
		if (driver == null && this.getDriverName() == null) {
			ok = ok && true;
		} else {
			ok = ok && driver.equalsIgnoreCase(this.getDriverName());
		}
		return ok;
	}
	
}