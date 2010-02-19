package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;

public class AllFilterPlugIn extends AbstractPlugIn {

	@Override
	public boolean accepts(SourceManager sm, String sourceName) {
		return true;
	}

}
