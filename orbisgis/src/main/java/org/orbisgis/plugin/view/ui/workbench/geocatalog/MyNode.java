package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class MyNode {
	private String name = null;
	private int type = 0;
	private File file = null;
	private String[] parameters = null;
	private DefaultMutableTreeNode treeNode = null;
	private String driver = null;
	
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
	
	
	public String toString() {
		return name;
	}
	
	public int getType() {
		return type;
	}
	
	public DefaultMutableTreeNode getTreeNode() {
		return treeNode;
	}
	
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

}