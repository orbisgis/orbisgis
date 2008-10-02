package org.orbisgis.actions;

import org.orbisgis.Services;
import org.orbisgis.action.IAction;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.updates.UpdateManager;

public class Update implements IAction {

	@Override
	public void actionPerformed() {
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
				UpdateManager um = Services.getService(UpdateManager.class);
				um.run();
				if (um.getError() != null) {
					Services.getErrorManager().error("Cannot retrieve update",
							um.getError());
				}
			}

			@Override
			public String getTaskName() {
				return "Searching updates";
			}
		});
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
