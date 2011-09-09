package org.orbisgis.core.geocognition;

public interface GeocognitionListener {

	/**
	 * Called when an element is going to be removed from the geocognition. This
	 * element can be a folder containing child elements but only one event will
	 * be triggered for the parent
	 * 
	 * @param geocognition
	 * @param element
	 * @return true if the operation can be done, false if the removal is not
	 *         possible
	 */
	boolean elementRemoving(Geocognition geocognition,
			GeocognitionElement element);

	/**
	 * Called when an element has been removed from the geocognition. This
	 * element can be a folder containing child elements but only one event will
	 * be triggered for the parent
	 * 
	 * @param geocognition
	 * @param element
	 */
	void elementRemoved(Geocognition geocognition, GeocognitionElement element);

	/**
	 * Called when an element has been added to the geocognition
	 * 
	 * @param geocognition
	 * @param parent
	 * @param newElement
	 */
	void elementAdded(Geocognition geocognition, GeocognitionElement parent,
			GeocognitionElement newElement);

	/**
	 * Called when an element has been added to the geocognition
	 * 
	 * @param geocognition
	 * @param element
	 *            The moved element
	 * @param oldParent
	 *            The parent that hosted the element before moving
	 */
	void elementMoved(Geocognition geocognition, GeocognitionElement element,
			GeocognitionElement oldParent);

}
