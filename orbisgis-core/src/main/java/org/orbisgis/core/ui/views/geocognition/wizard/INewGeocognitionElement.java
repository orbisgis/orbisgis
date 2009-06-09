package org.orbisgis.core.ui.views.geocognition.wizard;

import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.ui.wizards.IWizard;

public interface INewGeocognitionElement extends IWizard {

	/**
	 * Return the object and id that will be added to the geocognition. The
	 * object must be supported by some geocognition element factory so if a not
	 * supported element is returned the getFactory should not return null but a
	 * supporting factory
	 * 
	 * @throws GeocognitionException
	 *             If the element cannot be created
	 */
	void runWizard() throws GeocognitionException;

	/**
	 * Get the elements that where created in the last call to runWizard
	 * 
	 * @return
	 */
	int getElementCount();

	/**
	 * Return the index-th element created in the last call to runWizard
	 * 
	 * @param index
	 * @return
	 */
	Object getElement(int index);

	/**
	 * Return the name of the index-th element created in the last call to
	 * runWizard if it has a fixed name. Return null if a default automatically
	 * generated name is ok for the index-th element
	 * 
	 * @param index
	 * @return
	 */
	String getFixedName(int index);

	/**
	 * This factory will be added to the geocognition service. If the elements
	 * returned by the wizard are already supported it can return null
	 * 
	 * @return
	 */
	GeocognitionElementFactory[] getFactory();

	/**
	 * Gets an element used to draw the elements in the tree. If the default
	 * look is valid it can return null.
	 * 
	 * @return
	 */
	ElementRenderer getElementRenderer();

	/**
	 * Return true if the element returned by this wizard must have an id unique
	 * in all the geocognition. Otherwise an id unique in the folder where it is
	 * created will be used. If getFixedName returns something different than
	 * null this method is ignored
	 * 
	 * @param index
	 * @return
	 */
	boolean isUniqueIdRequired(int index);

	/**
	 * Gets the base name of the elements created with this wizard. This method
	 * is not called if getFixedName returns something different than null
	 * 
	 * @param elementIndex
	 *            index of the element in the list of the elements this wizard
	 *            is creating
	 * 
	 * @return
	 */
	String getBaseName(int elementIndex);
}
