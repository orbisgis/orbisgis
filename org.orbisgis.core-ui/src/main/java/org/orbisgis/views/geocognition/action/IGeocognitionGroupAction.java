package org.orbisgis.views.geocognition.action;

import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;

public interface IGeocognitionGroupAction {

	/**
	 * Executes this action on the specified elements. This method will be
	 * invoked only for those cases where the accept methods return true
	 * 
	 * @param geocognition
	 * @param element
	 *            The selected element or null if there is no selection
	 */
	void execute(Geocognition geocognition, GeocognitionElement[] elements);

	/**
	 * Return true if this action should be enabled for the specified elements.
	 * Return false otherwise
	 * 
	 * @param geocog
	 * @param element
	 *            The selected element or null if there is no selection
	 * @return
	 */
	boolean accepts(Geocognition geocog, GeocognitionElement[] elements);

}
