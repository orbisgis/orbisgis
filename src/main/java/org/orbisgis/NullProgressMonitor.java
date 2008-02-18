package org.orbisgis;

public class NullProgressMonitor implements IProgressMonitor {

	public void endTask() {
	}

	public int getProgress() {
		return 0;
	}

	public void init(String taskName) {
	}

	public void progressTo(int progress) {
	}

	public void startTask(String taskName, int percentage) {
	}

	public boolean isCancelled() {
		return false;
	}

}
