package org.orbisgis.geoview.images;

import javax.swing.ImageIcon;

/**
 * Gets an icon from this class' package.
 */
public class IconLoader {
	public static ImageIcon getIcon(String filename) {
		return new ImageIcon(IconLoader.class.getResource(filename));
	}
}