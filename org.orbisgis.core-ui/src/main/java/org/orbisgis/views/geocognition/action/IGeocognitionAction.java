package org.orbisgis.views.geocognition.action;

import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;

public interface IGeocognitionAction {

	/**
	 * Executes this action on the specified element. This method will be
	 * invoked only for those cases where the accept methods return true
	 * 
	 * @param geocognition
	 * @param element
	 *            The selected element or null if there is no selection
	 */
	void execute(Geocognition geocognition, GeocognitionElement element);

	/**
	 * Return true if this action should be enabled when there are the specified
	 * number of selected elements in the geocognition view. Return false
	 * otherwise
	 * 
	 * @param geocog
	 * @param selectionCount
	 * @return
	 */
	boolean acceptsSelectionCount(Geocognition geocog, int selectionCount);

	/**
	 * Return true if this action should be enabled for the specified element.
	 * Return false otherwise
	 * 
	 * @param geocog
	 * @param element
	 *            The selected element or null if there is no selection
	 * @return
	 */
	boolean accepts(Geocognition geocog, GeocognitionElement element);

}
