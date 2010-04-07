package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;

public class DBs implements IFilter {

	@Override
	public boolean accepts(SourceManager sm, String sourceName) {
		int type = sm.getSource(sourceName).getType();
		return (type & SourceManager.DB) == SourceManager.DB;
	}

}
