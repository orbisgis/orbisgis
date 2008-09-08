package org.orbisgis.views.geocognition.filters;

import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.views.geocognition.filter.IGeocognitionFilter;

public class SQL implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return new GeocognitionFunctionFactory().acceptContentTypeId(typeId)
				|| new GeocognitionCustomQueryFactory()
						.acceptContentTypeId(typeId);
	}

}
