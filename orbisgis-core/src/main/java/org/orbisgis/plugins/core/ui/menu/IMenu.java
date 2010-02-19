package org.orbisgis.plugins.core.ui.menu;

import javax.swing.JComponent;

import org.orbisgis.plugins.core.ui.PlugIn;

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
}
