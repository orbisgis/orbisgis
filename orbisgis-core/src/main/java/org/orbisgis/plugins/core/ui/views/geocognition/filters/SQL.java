package org.orbisgis.plugins.core.ui.views.geocognition.filters;

import org.orbisgis.plugins.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionFunctionFactory;

public class SQL implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return new GeocognitionFunctionFactory().acceptContentTypeId(typeId)
				|| new GeocognitionCustomQueryFactory()
						.acceptContentTypeId(typeId);
	}

}
