package org.orbisgis.core.ui.preferences.lookandfeel.images;

import java.util.ResourceBundle;

public class Icon {
	
	public static ResourceBundle rb = ResourceBundle.getBundle(
			"org/orbisgis/core/ui/preferences/lookandfeel/images/properties/images");
	
	public static String get(final String key) {
		try {
			return rb.getString(key);
		} catch (java.util.MissingResourceException e) {			
			return key;
		}
	}	

}
