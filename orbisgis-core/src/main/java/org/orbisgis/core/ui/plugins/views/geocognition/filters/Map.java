package org.orbisgis.core.ui.plugins.views.geocognition.filters;

public class Map implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return "org.orbisgis.core.geocognition.MapContext"
				.equals(typeId);
	}

}
