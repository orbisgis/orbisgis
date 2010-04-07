package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;

public class AllFilterPlugIn implements IFilter {

	@Override
	public boolean accepts(SourceManager sm, String sourceName) {
		return true;
	}

}
