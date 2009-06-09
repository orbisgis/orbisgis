package org.orbisgis.core.ui.views.geocognition.filter;

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
