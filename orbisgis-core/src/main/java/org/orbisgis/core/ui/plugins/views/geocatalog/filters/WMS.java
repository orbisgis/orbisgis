package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;

public class WMS extends AbstractPlugIn {

	@Override
	public boolean accepts(SourceManager sm, String sourceName) {
		int type = sm.getSource(sourceName).getType();
		return (type & SourceManager.WMS) == SourceManager.WMS;
	}

}
