package org.orbisgis;

public interface IProgressMonitor {

	public abstract void init(String taskName);

	/**
	 * Adds a new child task to the last added
	 *
	 * @param taskName
	 *            Task name
	 * @param percentage
	 *            percentage of the parent task that this task takes
	 */
	public abstract void startTask(String taskName, int percentage);

	public abstract void endTask();

	/**
	 * Indicates the progress of the last added task
	 *
	 * @param i
	 */
	public abstract void progressTo(int progress);

	public abstract int getProgress();

}