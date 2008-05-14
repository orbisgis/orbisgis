package org.orbisgis.pluginManager.background;

public interface BackgroundManager {

	/**
	 * Executes an operation in a background thread. This method blocks the
	 * interface so no more operation than cancel can be done
	 *
	 * @param lp
	 *            instance that executes the action.
	 */
	void backgroundOperation(BackgroundJob lp);

	/**
	 * Executes an operation in a background thread.
	 *
	 * @param lp
	 *            instance that executes the action.
	 */
	void nonBlockingBackgroundOperation(BackgroundJob lp);

	/**
	 * Executes an operation in a background thread. If there already exists an
	 * operation being executed or waiting to be executed with the same JobId as
	 * specified in this method the job is replaced
	 *
	 * @param processId
	 * @param lp
	 */
	void backgroundOperation(JobId processId, BackgroundJob lp);

	/**
	 * Executes an operation in a background thread without blocking the
	 * interface. If there already exists an operation being executed or waiting
	 * to be executed with the same JobId as specified in this method the job is
	 * replaced
	 *
	 * @param processId
	 * @param lp
	 */
	void nonBlockingBackgroundOperation(JobId processId, BackgroundJob lp);

	/**
	 * Gets a reference to the job queue
	 *
	 * @return
	 */
	JobQueue getJobQueue();

	/**
	 * Adds a listener to the Background process system
	 *
	 * @param listener
	 */
	void addBackgroundListener(BackgroundListener listener);

	/**
	 * Removes a listener from the Background process system
	 *
	 * @param listener
	 */
	void removeBackgroundListener(BackgroundListener listener);

}
