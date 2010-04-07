package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;

public interface IFilter {
	public boolean accepts(SourceManager sm, String sourceName);
}
