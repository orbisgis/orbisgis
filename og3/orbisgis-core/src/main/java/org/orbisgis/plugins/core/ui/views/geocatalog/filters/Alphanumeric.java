package org.orbisgis.plugins.core.ui.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;

public class Alphanumeric extends AbstractPlugIn {

	@Override
	public boolean accepts(SourceManager sm, String sourceName) {
		int type = sm.getSource(sourceName).getType();
		int spatial = SourceManager.VECTORIAL | SourceManager.RASTER
				| SourceManager.WMS;
		return (type & spatial) == 0;
	}

}