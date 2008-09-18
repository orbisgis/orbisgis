package org.orbisgis.views.geocognition.sync.editor;

import org.orbisgis.geocognition.GeocognitionElement;

/**
 * Listener for the elements in the compare editor. Used for the element changes
 * outside the editor as well as the saving events in the editor
 * 
 * @author victorzinho
 */
public interface EditorElementListener {

	/**
	 * Called when the content of the element has changed
	 * 
	 * @param e
	 *            the element changed
	 */
	void elementContentChanged(GeocognitionElement e);
}
