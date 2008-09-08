package org.orbisgis.views.geocognition.filters;

import org.orbisgis.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.views.geocognition.filter.IGeocognitionFilter;

public class Symbology implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return new GeocognitionSymbolFactory().acceptContentTypeId(typeId)
				|| new GeocognitionLegendFactory()
						.acceptContentTypeId(typeId);
	}

}
