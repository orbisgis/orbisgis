package org.orbisgis.geocognition;

public interface GeocognitionElementListener {

	/**
	 * Called when the id of the element has changed
	 * 
	 * @param element
	 */
	void idChanged(GeocognitionElement element);

	/**
	 * Called when the contents of the element has been edited
	 * 
	 * @param element
	 */
	void contentChanged(GeocognitionElement element);

	/**
	 * Called when the element has been saved
	 * 
	 * @param element
	 */
	void saved(GeocognitionElement element);

}
