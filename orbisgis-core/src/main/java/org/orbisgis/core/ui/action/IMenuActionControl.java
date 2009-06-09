package org.orbisgis.core.ui.action;

public interface IMenuActionControl {

	/**
	 * Gets the group of the action that installed this control. Null if no
	 * group is specified
	 * 
	 * @return
	 */
	String getGroup();

	/**
	 * Gets the id of the menu or action that installed this control.
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Gets the menu text in the current locale
	 * 
	 * @return
	 */
	String getText();

	/**
	 * Sets the action adapter that manages this menu
	 * 
	 * @param actionAdapter
	 */
	void setActionAdapter(IActionAdapter actionAdapter);

}
