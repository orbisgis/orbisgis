package org.orbisgis.core.ui.views.geocognition.filters;

import org.orbisgis.core.ui.views.geocognition.filter.IGeocognitionFilter;

public class Map implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return "org.orbisgis.core.geocognition.MapContext".equals(typeId);
	}

}
