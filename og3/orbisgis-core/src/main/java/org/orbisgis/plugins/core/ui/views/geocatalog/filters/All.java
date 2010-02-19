package org.orbisgis.plugins.core.ui.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;

public class All extends AbstractPlugIn {

	@Override
	public boolean accepts(SourceManager sm, String sourceName) {
		return true;
	}

}
