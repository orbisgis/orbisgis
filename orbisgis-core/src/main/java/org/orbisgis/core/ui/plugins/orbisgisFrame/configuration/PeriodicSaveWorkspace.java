package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.workspace.DefaultWorkspace;

public class PeriodicSaveWorkspace extends Thread {

	DefaultWorkspace workpsace;

	public long periodicTimeToSaveWrksp;
	private boolean stopSaving = false;

	public void setPeriodicTimeToSaveWrksp(long periodicTimeToSaveWrksp) {
		this.periodicTimeToSaveWrksp = periodicTimeToSaveWrksp;
	}

	public PeriodicSaveWorkspace(DefaultWorkspace workpsace) {
		this.workpsace = workpsace;
	}

	public void run() {
		try {
			while (!stopSaving) {
				workpsace.saveWorkspace();
				Thread.sleep(periodicTimeToSaveWrksp);
			}
		} catch (InterruptedException e) {

		} catch (Exception e) {
			ErrorMessages.error(ErrorMessages.CannotSaveWorkspace, e);
		}
		stopSaving = false;
	}

	public synchronized void stopSaving() {
		stopSaving = true;
	}

}
