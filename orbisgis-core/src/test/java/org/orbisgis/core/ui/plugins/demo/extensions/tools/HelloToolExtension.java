package org.orbisgis.core.ui.plugins.demo.extensions.tools;

import org.orbisgis.core.ui.pluginSystem.Extension;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;

public class HelloToolExtension extends Extension {

	public void configure(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getInfoToolBar().addAutomaton(
				new HelloTool(), IconLoader.getIcon("hello.png"));
	}
}
