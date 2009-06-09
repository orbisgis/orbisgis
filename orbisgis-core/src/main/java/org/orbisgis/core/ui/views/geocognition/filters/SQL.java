package org.orbisgis.core.ui.views.geocognition.filters;

import org.orbisgis.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.core.ui.views.geocognition.filter.IGeocognitionFilter;

public class SQL implements IGeocognitionFilter {

	@Override
	public boolean accept(String typeId) {
		return new GeocognitionFunctionFactory().acceptContentTypeId(typeId)
				|| new GeocognitionCustomQueryFactory()
						.acceptContentTypeId(typeId);
	}

}
