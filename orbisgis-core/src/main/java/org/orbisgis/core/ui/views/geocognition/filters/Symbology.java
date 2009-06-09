package org.orbisgis.core.ui.views.geocognition.filters;

import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.core.ui.views.geocognition.filter.IGeocognitionFilter;

public class Symbology implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return new GeocognitionSymbolFactory().acceptContentTypeId(typeId)
				|| new GeocognitionLegendFactory()
						.acceptContentTypeId(typeId);
	}

}
