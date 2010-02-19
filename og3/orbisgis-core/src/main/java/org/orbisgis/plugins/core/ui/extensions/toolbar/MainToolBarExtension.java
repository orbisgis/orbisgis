package org.orbisgis.plugins.core.ui.extensions.toolbar;

import org.orbisgis.plugins.core.ui.Extension;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.actions.ExitPlugIn;
import org.orbisgis.plugins.core.ui.actions.SavePlugIn;

public class MainToolBarExtension extends Extension {

	@Override
	public void configure(PlugInContext context) throws Exception {
		new ExitPlugIn().initialize(context);
		new SavePlugIn().initialize(context);
	}

}
