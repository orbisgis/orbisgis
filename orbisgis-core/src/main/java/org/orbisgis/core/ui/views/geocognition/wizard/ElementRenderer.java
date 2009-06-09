package org.orbisgis.core.ui.views.geocognition.wizard;

import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.core.geocognition.GeocognitionElement;

public interface ElementRenderer {

	/**
	 * Gets an icon to show in the geocognition tree for the elements with the
	 * specified id
	 * 
	 * @param contentTypeId
	 *            The id of the element
	 * @param properties
	 *            Properties of the element which icon is asked for.
	 * @return
	 */
	Icon getIcon(String contentTypeId, Map<String, String> properties);

	/**
	 * Get a default icon to show the elements with the specified id
	 * 
	 * @param contentTypeId
	 * @return
	 */
	Icon getDefaultIcon(String contentTypeId);
	
	/**
	 * Gets the tooltip that will be shown in the specified GeocognitionElement
	 * 
	 * @param element
	 * @return
	 */
	String getTooltip(GeocognitionElement element);
}
