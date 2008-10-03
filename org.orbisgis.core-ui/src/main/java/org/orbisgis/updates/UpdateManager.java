package org.orbisgis.updates;


public interface UpdateManager {

	/**
	 * Starts a thread that calls the {@link #run()} method
	 */
	public abstract void startSearch();

	/**
	 * searches for automatic updates and shows a dialog to install them if
	 * there is any
	 */
	public abstract void run();

	/**
	 * Apply the updates found in {@link #run()}
	 * 
	 * @return
	 */
	public abstract void applyUpdates();

	/**
	 * Returns an exception if the {@link #run()} method failed in their last
	 * call. If the method has not been executed or the execution was successful
	 * it returns null
	 * 
	 * @return
	 */
	public abstract Exception getError();

}