package org.orbisgis.editor;

import org.orbisgis.action.MenuTree;
import org.orbisgis.action.ToolBarArray;

public interface IExtensionPointEditor extends IEditor {

	/**
	 * Adds the parent menus and tool bars to the main window. This will be the
	 * parents of the entries installed in the installExtensionPoint method
	 * 
	 * @param menuTree
	 * @param toolBarArray
	 */
	void prepareMenus(MenuTree menuTree, ToolBarArray toolBarArray);

	/**
	 * Adds Menus and tool bars to the main window. There are easier ways to
	 * install actions that deal with a concrete editor. it should be used only
	 * if the editor needs to keep track of all the actions for some reason
	 * 
	 * @param menuTree
	 * @param toolBarArray
	 */
	void installExtensionPoint(MenuTree menuTree, ToolBarArray toolBarArray);

}
