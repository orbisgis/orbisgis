package org.orbisgis.core.edition;


public interface EditableElement extends EditableBaseElement {

	/**
	 * Return the id of the element
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Adds a listener to this element events
	 * 
	 * @param listener
	 */
	void addElementListener(EditableElementListener listener);

	/**
	 * Removes a listener to this element events
	 * 
	 * @param listener
	 * @return The removed listener or null if there was not such listener
	 */
	boolean removeElementListener(EditableElementListener listener);

	/**
	 * Return if this element was modified since the last time save or open was
	 * called
	 * 
	 * @return
	 */
	boolean isModified();
}
