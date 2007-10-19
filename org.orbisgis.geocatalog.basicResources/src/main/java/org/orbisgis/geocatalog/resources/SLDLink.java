package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.orbisgis.geocatalog.resources.BasicResource;
import org.orbisgis.geocatalog.resources.IResource;

public class SLDLink extends BasicResource {

	private final Icon icon = new ImageIcon(getClass().getResource(
			"sldlink.png"));

	private SLDFile sld = null;

	public SLDLink(String name, SLDFile sld) {
		super(name);
		this.sld = sld;
	}

	public void addChild(IResource child, int index) {
		// A SLD file cannot have children
	}

	public Icon getIcon(boolean isExpanded) {
		return icon;
	}

	public SLDFile getSld() {
		return sld;
	}

}
