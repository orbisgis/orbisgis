package org.orbisgis.updates;

import java.io.File;
import java.util.ArrayList;

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
	 * Gets the update files found in {@link #run()}
	 * 
	 * @return
	 */
	public abstract ArrayList<File> getUpdateFiles();

	/**
	 * Returns an exception if the {@link #run()} method failed in their last
	 * call. If the method has not been executed or the execution was successful
	 * it returns null
	 * 
	 * @return
	 */
	public abstract Exception getError();

}