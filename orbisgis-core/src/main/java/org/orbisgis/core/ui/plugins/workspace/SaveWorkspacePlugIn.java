package org.orbisgis.core.ui.plugins.workspace;

import java.io.IOException;
import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.workspace.Workspace;

public class SaveWorkspacePlugIn extends AbstractPlugIn {

	private JMenuItem menuItem;

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		try {
			ws.saveWorkspace();
		} catch (IOException e) {
			Services.getErrorManager().error("Cannot save workspace", e);
		}
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.FILE }, Names.SAVE_WS, false,
				getIcon(IconNames.SAVE_WS_ICON), null, null, context);
	}

	@Override
	public void update(Observable o, Object arg) {
		menuItem.setEnabled(isEnabled());
		menuItem.setVisible(isVisible());

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}
}
