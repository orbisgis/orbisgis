package org.orbisgis.progress;

public class NullProgressMonitor implements IProgressMonitor {

	public void endTask() {
	}

	public void init(String taskName) {
	}

	public void progressTo(int progress) {
	}

	public boolean isCancelled() {
		return false;
	}

	public String getCurrentTaskName() {
		return null;
	}

	public int getOverallProgress() {
		return 0;
	}

	public void startTask(String taskName) {
	}

	public int getCurrentProgress() {
		return 0;
	}

}
