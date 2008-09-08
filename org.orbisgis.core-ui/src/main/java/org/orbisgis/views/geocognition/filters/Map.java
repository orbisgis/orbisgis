package org.orbisgis.views.geocognition.filters;

import org.orbisgis.views.geocognition.filter.IGeocognitionFilter;

public class Map implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return "org.orbisgis.geocognition.MapContext".equals(typeId);
	}

}
