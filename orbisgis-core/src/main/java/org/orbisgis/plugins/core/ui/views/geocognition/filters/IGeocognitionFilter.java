package org.orbisgis.plugins.core.ui.views.geocognition.filters;

public interface IGeocognitionFilter {

	/**
	 * Return true to make the elements with this type appear in the
	 * geocognition
	 * 
	 * @param typeId
	 * @return
	 */
	boolean accept(String typeId);

}
