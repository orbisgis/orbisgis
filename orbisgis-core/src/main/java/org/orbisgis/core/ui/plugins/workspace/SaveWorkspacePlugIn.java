package org.orbisgis.core.ui.plugins.workspace;

import java.io.IOException;

import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.progress.IProgressMonitor;

public class SaveWorkspacePlugIn extends AbstractPlugIn {

	private JMenuItem menuItem;
	
	public boolean execute(PlugInContext context) throws Exception {
		BackgroundManager mb = Services.getService(BackgroundManager.class);
		mb.backgroundOperation(new BackgroundJob() {
			Workspace ws = (Workspace) Services.getService(Workspace.class);

			@Override
			public void run(IProgressMonitor pm) {
				try {
					ws.saveWorkspace();
				} catch (IOException e) {
					Services.getErrorManager()
							.error("Cannot save workspace", e);
				}
			}

			@Override
			public String getTaskName() {
				return "Saving Workspace";
			}
		});
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.FILE }, Names.SAVE_WS, false,
				getIcon(IconNames.SAVE_WS_ICON), null, null, context);
	}
	
	public boolean isEnabled() {
		menuItem.setEnabled(true);
		return true;
	}
}
