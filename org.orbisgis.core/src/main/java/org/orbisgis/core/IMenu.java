package org.orbisgis.core;


import javax.swing.JComponent;

public interface IMenu {

	public abstract JComponent getJMenuItem();

	public abstract void groupMenus();

	public String getId();

	public String getParent();

	public String getText();

	public abstract String getGroup();

	public abstract void addChild(IMenu menu);

	public abstract IMenu[] getChilds();

}