package org.orbisgis.core.configuration;

public interface BasicConfiguration {
	/**
	 * Sets the configuration property
	 * 
	 * @param key
	 *            the key of the configuration property
	 * @param value
	 *            the value of the configuration property
	 * @return the previous value of the configuration key or <code>null</code>
	 *         if none
	 */
	public String setProperty(String key, String value);

	/**
	 * Gets the value of the configuration property
	 * 
	 * @param key
	 *            the key of the configuration property
	 * @return the value of the configuration property
	 */
	public String getProperty(String key);

	/**
	 * Saves the configuration to a persistent source
	 */
	public void save();

	/**
	 * Loads the configuration from a persistent source
	 */
	public void load();

	/**
	 * Removes the configuration property
	 * 
	 * @param key
	 *            the key of the property to remove
	 * @return the value of the removed property or <code>null</code> if none
	 */
	public String removeProperty(String key);
}
