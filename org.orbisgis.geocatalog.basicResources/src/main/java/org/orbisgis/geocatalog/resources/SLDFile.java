package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.resources.FileResource;

public class SLDFile extends FileResource {

	private final Icon icon = new ImageIcon(getClass().getResource(
			"sld_file.png"));

	public SLDFile(String name, String filePath) {
		super(name, filePath);
	}

	public void addChild(IResource child, int index) {
		// A SLD file cannot have children
	}

	public Icon getIcon(boolean isExpanded) {
		return icon;
	}
}
