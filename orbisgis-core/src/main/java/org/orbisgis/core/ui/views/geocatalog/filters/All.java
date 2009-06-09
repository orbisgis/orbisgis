package org.orbisgis.core.ui.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.core.ui.views.geocatalog.filter.IGeocatalogFilter;

public class All implements IGeocatalogFilter {

	@Override
	public boolean accept(SourceManager sm, String sourceName) {
		return true;
	}

}
