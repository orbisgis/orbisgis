package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Class MyNode
 * 
 * Each node of the Catalog tree is linked with an instance of MyNode. It
 * contains the type of the node (folder, datasource, sldfile,...).
 * 
 * @author Samuel CHEMLA TODO : implement our own treeModel in Catalog so each
 *         instance of MyNode *IS* a node of the tree but not an Object linked
 *         to the tree DefaultMutableTreeNodes
 * 
 */
public class MyNode {
	private String name = null;

	private int type = 0;

	private File file = null;

	private DefaultMutableTreeNode treeNode = null;

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
	}

	public MyNode(String name, int type, String driver, File file) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.file = file;
	}

	public MyNode(String name, int type, String query) {
		this.name = name;
		this.type = type;
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

	/** Retrieves the name of the node */
	public String toString() {
		return name;
	}

	/** Retrieves the type of a node (folder, datasource, sld...) */
	public int getType() {
		return type;
	}

	/** Retrieves the DefaultMutableTreeNode associated with the node (this) */
	public DefaultMutableTreeNode getTreeNode() {
		return treeNode;
	}

	/**
	 * Allows to set the link between the node (this) and its
	 * DefaultMutableTreeNode
	 * 
	 */
	public void setTreeNode(DefaultMutableTreeNode node) {
		this.treeNode = node;
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
}