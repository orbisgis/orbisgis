package org.orbisgis.plugins.core.ui.workspace;

import java.io.IOException;
import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.workspace.Workspace;

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
				getIcon(Names.SAVE_WS_ICON), null, null, null, null, null);
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("SavePlugIn : " + arg);
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
