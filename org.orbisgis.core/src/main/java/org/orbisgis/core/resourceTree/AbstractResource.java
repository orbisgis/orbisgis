package org.orbisgis.core.resourceTree;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class AbstractResource implements IResource {

	protected IResource parent = null;

	public Icon getIcon(boolean isExpanded) {
		java.net.URL url = this.getClass().getResource(
				"/org/orbisgis/geocatalog/mini_orbisgis.png");
		return (new ImageIcon(url));
	}

	public void setParent(IResource parent) {
		this.parent = parent;
	}

	public void move(IResource dropNode) {
		this.removeFrom(parent);
		this.addTo(dropNode);
	}

}
