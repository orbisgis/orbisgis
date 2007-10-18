package org.orbisgis.geocatalog;

import javax.swing.JMenuItem;


/**
 * An interface to add popup entries in GeoCatalog
 *
 * @author Samuel CHEMLA
 *
 */
public interface IPopupAction {
	/**
	 * Retrieves an array of items to put in the popup menu when the user right
	 * clicks.
	 *
	 * @return
	 */
	public JMenuItem[] getPopupActions();

	/**
	 * You will be given the instance of catalog as soon as your class will be
	 * instanciated.
	 *
	 * @param catalog
	 */
	public void setCatalog(Catalog catalog);
}
