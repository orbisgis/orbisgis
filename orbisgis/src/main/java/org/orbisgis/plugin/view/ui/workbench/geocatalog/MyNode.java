package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

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
	
	MyNode(String name, int type) {
		this.name = name;
		this.type = type;
	}
	
	MyNode(String name, int type, String driver, File file) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.file = file;
	}
	
	MyNode(String name, int type, String query) {
		this.name = name;
		this.type = type;
		this.query = query;
	}
	
	/** Changes the name of a node
	 * 
	 * @param newName The new name for the node
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
	
	/** Retrieves the DefaultMutableTreeNode associated with the node (this)
	 * 
	 * @TODO : implement our own treeModel
	 */
	public DefaultMutableTreeNode getTreeNode() {
		return treeNode;
	}
	
	/** Allows to set the link between the node (this) and its DefaultMutableTreeNode
	 * 
	 * @TODO : see getTreeNode()
	 */
	public void setTreeNode(DefaultMutableTreeNode node) {
		this.treeNode = node;
	}
	
	public String getDriverName() {
		return driver;
	}
	
	public File getFile() {
		return file;
	}
	
	public MyNode createLink() {
		MyNode node = new MyNode(this.name,MyNode.sldlink,null,this.file);
		return node;
	}
	
	public String getQuery() {
		return query;
	}
}