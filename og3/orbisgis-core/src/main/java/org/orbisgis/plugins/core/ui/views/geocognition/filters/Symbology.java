package org.orbisgis.plugins.core.ui.views.geocognition.filters;

import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionSymbolFactory;

public class Symbology implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return new GeocognitionSymbolFactory().acceptContentTypeId(typeId)
				|| new GeocognitionLegendFactory().acceptContentTypeId(typeId);
	}

}
