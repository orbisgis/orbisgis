package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Class MyNode
 * 
 * Each node of the Catalog tree is an instance of MyNode. It contains the type
 * of the node (folder, datasource, sldfile,...) and links to children and
 * parent.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class MyNode {

	/**
	 * The persistence string mention all the fields which should be saved
	 * during serialization. Please keep them in order and keep them
	 * synchronized with the complete constructor.
	 */
	public static final String[] persistenceString = new String[] { "name",
			"type", "driverName", "file", "query", "parent", "children" };

	public final static int folder = 0;

	public final static int datasource = 1;

	public final static int sldfile = 2;

	public final static int sqlquery = 3;

	public final static int sldlink = 4;

	public final static int raster = 5;

	private String name = null;

	private int type = 0;

	private String driverName = null;

	private File file = null;

	private String query = null;

	private MyNode parent = null;

	// TODO Do we keep Vector or do we use ArrayList ?
	// It seem that the only difference is that Vector use synchronized methods
	private Vector<MyNode> children = null;

	/**
	 * Complete constructor. It must have the parameters mentionned in the field
	 * persistenceString (see above).
	 * 
	 * @param name
	 *            The name of the node
	 * @param type
	 *            Its type. See MyNode static fields.
	 * @param driverName
	 *            The name of the driver used. Use static fields DRIVER_NAME
	 *            defined in each Driver.
	 * @param file
	 *            The object of type File used to keep a references to files.
	 * @param query
	 *            The query of the node.
	 * @param father
	 *            The father of the node.
	 * @param newChildren
	 *            Its children.
	 */
	public MyNode(String name, int type, String driverName, File file,
			String query, MyNode father, Vector<MyNode> newChildren) {
		this.name = name;
		this.type = type;
		this.driverName = driverName;
		this.file = file;
		this.query = query;
		this.parent = father;

		if (newChildren == null) {
			newChildren = new Vector<MyNode>();
		}

		this.children = newChildren;

	}
/*
	public MyNode(String name, int type, String driverName, String query,
			MyNode father, Vector<MyNode> newChildren) {
		this(name, type, driverName, null, query, father, newChildren);
	}*/

	public MyNode(String name, int type) {
		this(name, type, null, null, null, null, null);
	}

	public MyNode(String name, int type, String driverName, File file) {
		this(name, type, driverName, file, null, null, null);
	}

	public MyNode(String name, int type, String query) {
		this(name, type, null, null, query, null, null);
	}

	// public MyNode() {
	// System.out.println("called default constructor");
	// }

	/***************************************************************************
	 * 
	 * GETTERS
	 * 
	 * These methods are necessary for serialization. Do not delete them.
	 * 
	 **************************************************************************/

	public Vector<MyNode> getChildren() {
		return children;
	}

	/**
	 * Retrieves the name of the driverName. This is mostly used to make a
	 * distinction between all kind of datasources and choose some beautiful
	 * icons
	 * 
	 */
	public String getDriverName() {
		return driverName;
	}

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	public MyNode getParent() {
		return parent;
	}

	public String getQuery() {
		return query;
	}

	public int getType() {
		return type;
	}

	/***************************************************************************
	 * 
	 * END OF GETTERS
	 * 
	 **************************************************************************/

	/**
	 * Changes the name of a node
	 * 
	 * @param newName
	 *            The new name for the node
	 * 
	 */
	public void setName(String newName) {
		this.name = newName;
	}

	/**
	 * Changes the query of a node
	 * 
	 * @param newQuery
	 *            the new query
	 * 
	 */
	public void setQuery(String newQuery) {
		if (this.type != sqlquery) {
			System.err
					.println("WARNING : setting a SQL query for a node which is a type "
							+ type);
		}
		this.query = newQuery;
	}

	/**
	 * By default, toString() retrieves the name of the node
	 * 
	 * @return The name of the node
	 */
	public String toString() {
		return name;
	}

	/**
	 * Copy the node this and return a node of the type sldlink
	 * 
	 * @return a new node of type sldlink, containing the reference to the SLD
	 *         file
	 */
	public MyNode createLink() {
		MyNode node = new MyNode(this.name, MyNode.sldlink, null, this.file);
		return node;
	}

	/**
	 * Changes the parent of a node. Useful for moving nodes.
	 * 
	 * @param father
	 *            the new father of the node
	 */
	public void setParent(MyNode father) {
		this.parent = father;
	}

	/**
	 * Retrieves the child at position i
	 * 
	 * @param i :
	 *            index of desired child
	 * @return (MyNode) child
	 */
	public MyNode getChildAt(int i) {
		return (MyNode) children.get(i);
	}

	/**
	 * Returns the number of children for this node
	 * 
	 * @return
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * Return the index of the specified child
	 * 
	 * @return int
	 * 
	 */
	public int getIndexOfChild(MyNode kid) {
		return children.indexOf(kid);
	}

	/**
	 * Says if the node have children
	 * 
	 * @return true if the node have at least one child
	 */
	public boolean haveChildren() {
		return !children.isEmpty();
	}

	/**
	 * Add a child to this at last position.
	 * 
	 * @param child
	 *            The child you add
	 */
	public void add(MyNode child) {
		add(child, getChildCount());
	}

	/**
	 * Add a child to this at index position.
	 * 
	 * @param child
	 *            The child you add
	 * @param index
	 *            The position of the new child
	 */
	public void add(MyNode child, int index) {
		children.add(index, child);
		child.setParent(this);
	}

	/**
	 * Removes a child from this node
	 * 
	 * @param child
	 *            The child to remove
	 */
	public void remove(MyNode child) {
		children.remove(child);
	}

	// TODO This is the same as haveChildren()...
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	/**
	 * Retrieves the nodes to root First element is root, last element is this
	 * node
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

	/**
	 * TODO comment
	 * 
	 * @return
	 */
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

	/**
	 * TODO more comment
	 * 
	 * @param node
	 * @return true if this node and node have the same name and the same driver
	 */
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