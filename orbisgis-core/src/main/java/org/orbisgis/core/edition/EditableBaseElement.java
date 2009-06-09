package org.orbisgis.core.edition;

import org.orbisgis.progress.IProgressMonitor;

public interface EditableBaseElement {

	/**
	 * Return an unique String that identifies the element type
	 * 
	 * @return
	 */
	String getTypeId();

	/**
	 * Opens the element for edition. This method will typically be followed by
	 * some edition actions in the stored object, by calls to the save method
	 * and finally by a call to close
	 * 
	 * @param progressMonitor
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 * @throws EditableElementException
	 *             If the operation cannot be done
	 */
	void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException;

	/**
	 * Saves the status of the element so that next call to getJAXBElement
	 * reflects the changes
	 * 
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 * @throws EditableElementException
	 *             Indicates that the saving was successful but there were some
	 *             extraordinary conditions during the saving. The saving must
	 *             always be done
	 */
	void save() throws UnsupportedOperationException, EditableElementException;

	/**
	 * Closes the element. All resources should be freed and all memory should
	 * be released because there may be plenty of GeocognitionElements in closed
	 * state
	 * 
	 * @param progressMonitor
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 * @throws EditableElementException
	 *             If the closing was not done
	 */
	void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException;

	/**
	 * Gets the object stored in this element.
	 * 
	 * @return The object stored in this element or null if the element is not
	 *         supported
	 * @throws UnsupportedOperationException
	 *             If this element is a folder
	 */
	Object getObject() throws UnsupportedOperationException;

}
