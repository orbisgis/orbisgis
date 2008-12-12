package org.orbisgis.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.views.geocatalog.filter.IGeocatalogFilter;

public class All implements IGeocatalogFilter {

	@Override
	public boolean accept(SourceManager sm, String sourceName) {
		return true;
	}

}
