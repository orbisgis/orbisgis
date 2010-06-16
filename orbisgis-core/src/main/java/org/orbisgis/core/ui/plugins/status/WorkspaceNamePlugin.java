package org.orbisgis.core.ui.plugins.status;

import javax.swing.JButton;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;

public class WorkspaceNamePlugin extends AbstractPlugIn {

	private JButton btn;

	public WorkspaceNamePlugin() {
		btn = new JButton();
		DefaultWorkspace workspace = (DefaultWorkspace) Services
				.getService(Workspace.class);
		StringBuffer message = new StringBuffer("Workspace : ");
		message.append(workspace.getWorkspaceFolder());
		btn.setText(message.toString());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getMainStatusToolBar().addPlugIn(
				this, btn, context);

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
