package org.orbisgis.core.actions;

/**
 * Interface that manages the status and action of the controls installed in the
 * user interface that are related to some action extension point
 *
 * @author Fernando Gonzalez Cortes
 */
public interface IAction {

	/**
	 * Invoked when the control is triggered
	 */
	void actionPerformed();

	/**
	 * Should return the enable status of the ui control
	 *
	 * @return
	 */
	boolean isEnabled();

	/**
	 * Should return the visible status of the ui control
	 *
	 * @return
	 */
	boolean isVisible();

}
