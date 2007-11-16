package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;


public class SLDLink implements IResourceType {

	private final Icon icon = new ImageIcon(getClass().getResource(
			"sldlink.png"));

	private SLDFile sld = null;

	public SLDLink(SLDFile sld) {
		this.sld = sld;
	}

	public Icon getIcon(INode node, boolean isExpanded) {
		return icon;
	}

	public SLDFile getSld() {
		return sld;
	}

	public void addToTree(INode parent, INode toAdd)
			throws ResourceTypeException {
		throw new UnsupportedOperationException();
	}

	public void moveResource(INode src, INode dst) throws ResourceTypeException {
		throw new UnsupportedOperationException();
	}

	public void removeFromTree(INode toRemove) throws ResourceTypeException {
		throw new UnsupportedOperationException();
	}

	public void setName(INode node, String newName)
			throws ResourceTypeException {
		node.setName(newName);
	}

}
