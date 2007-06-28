package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class MyNode {
	private String name = null;
	private int type = 0;
	private File file = null;
	private String[] parameters = null;
	private String linkedDSName = null;
	private DefaultMutableTreeNode treeNode = null;
	
	final static int folder = 0;
	final static int datasource = 1;
	final static int sldfile = 2;
	final static int sqlquery = 3;
	final static int sldlink = 4;
	
	MyNode(String name, int type) {
		this.name = name;
		this.type = type;
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
}