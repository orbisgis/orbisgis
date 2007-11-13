package org.orbisgis.core;


import javax.swing.JComponent;
import javax.swing.JSeparator;

public class MenuSeparator implements IMenu {

	public JComponent getJMenuItem() {
		return new JSeparator();
	}

	public void groupMenus() {

	}

}
