package org.orbisgis.core.ui.pluginSystem.menu;

import javax.swing.JComponent;
import javax.swing.JSeparator;

import org.orbisgis.core.ui.pluginSystem.PlugIn;

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

	public IMenu[] getChildren() {
		return null;
	}

	public String getGroup() {
		return null;
	}

	public void remove(IMenu menuToDelete) {

	}

	public boolean hasAction() {
		return false;
	}

	public PlugIn getPlugin() {
		return null;
	}
	
	public boolean isSelectable() {		
		return false;
	}

	public void setSelected(boolean selected) {
	}

}
