package org.orbisgis.core.actions;

/**
 * Interface implemented by the ui controls that will receive in the refresh
 * method the signal to refresh their status
 *
 * @author Fernando Gonzalez Cortes
 */
public interface IActionControl {

	/**
	 * Method invoked when the control has to refresh its status
	 */
	void refresh();
}
