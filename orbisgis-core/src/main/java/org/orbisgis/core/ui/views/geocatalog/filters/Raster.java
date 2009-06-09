package org.orbisgis.core.ui.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.core.ui.views.geocatalog.filter.IGeocatalogFilter;

public class Raster implements IGeocatalogFilter {

	@Override
	public boolean accept(SourceManager sm, String sourceName) {
		int type = sm.getSource(sourceName).getType();
		return (type & SourceManager.RASTER) == SourceManager.RASTER;
	}

}