package org.orbisgis.views.geocognition.sync.editor;

import java.awt.Component;

import org.orbisgis.geocognition.GeocognitionElement;

public interface ICompareEditor {

	/**
	 * Gets the interface component of the editor
	 * 
	 * @return the interface component of the editor
	 */
	public Component getComponent();

	/**
	 * Sets the model of the editor
	 * 
	 * @param left
	 *            the left element to compare or null if none
	 * @param right
	 *            the right element to compare or null if none
	 */
	public void setModel(GeocognitionElement left, GeocognitionElement right);

	/**
	 * Checks if the editor accepts the specified content
	 * 
	 * @param contentTypeId
	 *            the id of the content to check
	 * @return true if the editor accepts the content, false otherwise
	 */
	public boolean accepts(String contentTypeId);

	/**
	 * Performs all the required operations when the editor is hidden
	 */
	public void close();

	/**
	 * Enables or disables the left element edition in the editor
	 * 
	 * @param b
	 *            flag to enable / disable
	 */
	public void setEnabledLeft(boolean b);

	/**
	 * Enables or disables the right element edition in the editor
	 * 
	 * @param b
	 *            flag to enable / disable
	 */
	public void setEnabledRight(boolean b);

	/**
	 * Gets the left element of the editor
	 * 
	 * @return the left element
	 */
	public GeocognitionElement getLeftElement();

	/**
	 * Gets the right element of the editor
	 * 
	 * @return the right element
	 */
	public GeocognitionElement getRightElement();

	/**
	 * Determines if the left element of the editor has been modified
	 * 
	 * @return true if it has been modified, false otherwise
	 */
	public boolean isLeftDirty();

	/**
	 * Determines if the right element of the editor has been modified
	 * 
	 * @return true if it has been modified, false otherwise
	 */
	public boolean isRightDirty();

	/**
	 * Saves the left element of the editor
	 */
	public void saveLeft();

	/**
	 * Saves the right element of the editor
	 */
	public void saveRight();
}
