package org.orbisgis.updates;

import java.net.URL;

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

	/**
	 * Returns true if the update manager should search for updates at start up
	 * or only when the user ask for it
	 * 
	 * @return
	 */
	public boolean isSearchAtStartup();

	/**
	 * Specify true to make OG search for updates just after the application is
	 * started. False will do nothing at startup
	 * 
	 * @param searchAtStartup
	 */
	public void setSearchAtStartup(boolean searchAtStartup);

	/**
	 * Get the site where the updates will be searched
	 * 
	 * @return
	 */
	public URL getUpdateSiteURL();

	/**
	 * Set the site where the updates will be searched
	 * 
	 * @param updateSiteURL
	 */
	public void setUpdateSiteURL(URL updateSiteURL);

}