package org.orbisgis.plugins.core.ui.views.geocognition.filters;

public class Map implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return "org.orbisgis.plugins.core.geocognition.MapContext"
				.equals(typeId);
	}

}
