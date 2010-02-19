package org.orbisgis.core.ui.plugins.views.geocognition.filters;

import org.orbisgis.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionFunctionFactory;

public class SQL implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return new GeocognitionFunctionFactory().acceptContentTypeId(typeId)
				|| new GeocognitionCustomQueryFactory()
						.acceptContentTypeId(typeId);
	}

}
