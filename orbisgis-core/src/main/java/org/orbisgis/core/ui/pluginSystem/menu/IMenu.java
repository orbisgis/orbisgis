package org.orbisgis.core.ui.pluginSystem.menu;

import javax.swing.JComponent;

import org.orbisgis.core.ui.pluginSystem.PlugIn;

public interface IMenu {

	public abstract JComponent getJMenuItem();	

	public abstract void groupMenus();

	public String getId();

	public String getParent();

	public PlugIn getPlugin();

	public String getText();

	public abstract String getGroup();

	public abstract void addChild(IMenu menu);

	public abstract IMenu[] getChildren();

	public abstract void remove(IMenu menuToDelete);

	public String toString();
	
	public boolean isSelectable();
	public void setSelected(boolean selected);
}
