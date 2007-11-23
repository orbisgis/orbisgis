package org.orbisgis.core.actions;


import javax.swing.JComponent;
import javax.swing.JSeparator;

public class MenuSeparator implements IMenu {

	public JComponent getJMenuItem() {
		return new JSeparator();
	}

	public void groupMenus() {

	}

	public String getId() {
		return null;
	}

	public String getParent() {
		return null;
	}

	public String getText() {
		return null;
	}

	public void addChild(IMenu menu) {
	}

	public IMenu[] getChilds() {
		return null;
	}

	public String getGroup() {
		return null;
	}

}
