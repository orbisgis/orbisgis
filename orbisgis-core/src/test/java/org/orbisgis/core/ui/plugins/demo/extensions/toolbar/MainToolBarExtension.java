package org.orbisgis.core.ui.plugins.demo.extensions.toolbar;

import org.orbisgis.core.ui.pluginSystem.Extension;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.actions.ExitPlugIn;
import org.orbisgis.core.ui.plugins.actions.SavePlugIn;

public class MainToolBarExtension extends Extension {

	@Override
	public void configure(PlugInContext context) throws Exception {
		new ExitPlugIn().initialize(context);
		new SavePlugIn().initialize(context);
	}

}
