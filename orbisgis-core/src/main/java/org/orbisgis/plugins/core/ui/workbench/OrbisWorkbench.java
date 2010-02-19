package org.orbisgis.plugins.core.ui.workbench;

import java.io.File;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.PlugInManager;
import org.orbisgis.plugins.core.ui.windows.mainFrame.OrbisGISFrame;

//craete WorkbenchContext
public class OrbisWorkbench {

	private WorkbenchContext context;
	private PlugInManager plugInManager;

	public PlugInManager getPlugInManager() {
		return plugInManager;
	}

	private OrbisGISFrame frame;

	public OrbisWorkbench(OrbisGISFrame frame) {
		context = new OrbisWorkbenchContext(this);
		Services.registerService(WorkbenchContext.class,
				"Gives access to the current WorkbenchContext", this.context);
		this.frame = frame;
	}

	public OrbisWorkbench() {
		context = new OrbisWorkbenchContext(this);
	}

	public void runWorkbench() {
		File extensionsDirectory = extensionsDirectory = new File("lib/ext");
		OrbisConfiguration setup = new OrbisConfiguration();
		try {
			plugInManager = new PlugInManager(context, extensionsDirectory);
			setup.setup(context);
			context.getWorkbench().getPlugInManager().load();
			context.setLastAction("Orbisgis started");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public OrbisGISFrame getFrame() {
		return frame;
	}

	public WorkbenchContext getWorkbenchContext() {
		return context;
	}
}
