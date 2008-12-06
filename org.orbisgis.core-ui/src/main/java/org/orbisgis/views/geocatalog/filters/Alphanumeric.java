package org.orbisgis.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.views.geocatalog.filter.IGeocatalogFilter;

public class Alphanumeric implements IGeocatalogFilter {

	@Override
	public boolean accept(SourceManager sm, String sourceName) {
		int type = sm.getSource(sourceName).getType();
		int spatial = SourceManager.VECTORIAL | SourceManager.RASTER
				| SourceManager.WMS;
		return (type & spatial) == 0;
	}

}