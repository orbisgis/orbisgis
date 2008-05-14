package org.orbisgis.pluginManager.background;

public interface BackgroundListener {

	/**
	 * Invoked when a new job has been enqueued
	 *
	 * @param job
	 */
	public void jobAdded(Job job);

	/**
	 * Invoked when an existing job has been removed, either finished, either
	 * cancelled
	 *
	 * @param job
	 */
	public void jobRemoved(Job job);

	/**
	 * Invoked when a job has been replaced in the queue
	 *
	 * @param job
	 */
	public void jobReplaced(Job job);

}
