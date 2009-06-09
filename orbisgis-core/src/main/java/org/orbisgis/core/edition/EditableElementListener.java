package org.orbisgis.core.edition;


public interface EditableElementListener {

	/**
	 * Called when the id of the element has changed
	 * 
	 * @param element
	 */
	void idChanged(EditableElement element);

	/**
	 * Called when the contents of the element has been edited
	 * 
	 * @param element
	 */
	void contentChanged(EditableElement element);

	/**
	 * Called when the element has been saved
	 * 
	 * @param element
	 */
	void saved(EditableElement element);

}
