package org.orbisgis.geocognition;

import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;

public interface GeocognitionCommonElement {

	/**
	 * Gets the object stored in this geocognition element
	 * 
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this element is a folder
	 */
	Object getObject() throws UnsupportedOperationException;

	/**
	 * Gets the JAXB generated object containing all meaningful information for
	 * the marshalling process
	 * 
	 * @return
	 */
	Object getJAXBObject();

	/**
	 * Gets the factory that created this element
	 * 
	 * @return
	 */
	GeocognitionElementFactory getFactory();

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
	 * @throws GeocognitionException
	 *             If the operation cannot be done
	 */
	void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, GeocognitionException;

	/**
	 * Saves the status of the element so that next call to getJAXBElement
	 * reflects the changes
	 * 
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 * @throws GeocognitionException
	 *             Indicates that the saving was successful but there were some
	 *             extraordinary conditions during the saving. The saving must
	 *             always be done
	 */
	void save() throws UnsupportedOperationException, GeocognitionException;

	/**
	 * Closes the element. All resources should be freed and all memory should
	 * be released because there may be plenty of GeocognitionElements in closed
	 * state
	 * 
	 * @param progressMonitor
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 */
	void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException;

}
