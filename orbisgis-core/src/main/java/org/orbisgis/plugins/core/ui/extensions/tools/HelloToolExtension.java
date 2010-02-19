package org.orbisgis.plugins.core.ui.extensions.tools;

import org.orbisgis.plugins.core.ui.Extension;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public class HelloToolExtension extends Extension {

	public void configure(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getInfoToolBar().addAutomaton(
				new HelloTool(), "hello.png");
	}
}
