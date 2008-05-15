package org.orbisgis.view;

import java.awt.Component;


/**
 * Interface to manage the views in the main application window
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface ViewManager {

	/**
	 * Gets the component of the view with the specified id
	 *
	 * @param viewId
	 * @return
	 */
	public Component getView(String viewId);

	/**
	 * Shows the view with the specified id
	 *
	 * @param id
	 */
	public void showView(String id);

	/**
	 * Hides the view with the specified id
	 *
	 * @param id
	 */
	public void hideView(String id);

	/**
	 * Gets the view in charge of managing the IEditor instances
	 *
	 * @return
	 */
	public IEditorsView getEditorsView();

}
