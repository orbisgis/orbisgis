package org.gdms.data;

public interface DataSourceFactoryListener {

	/**
	 * Called when some source is added to the system
	 * @param e
	 */
	public void sourceAdded(DataSourceFactoryEvent e);

	/**
	 * Called when some source is removed
	 * @param e
	 */
	public void sourceRemoved(DataSourceFactoryEvent e);

	/**
	 * Called when sql is executed
	 * @param event
	 */
	public void sqlExecuted(DataSourceFactoryEvent event);

	/**
	 * Called when the name of a datasource is changed
	 *
	 * @param e
	 */
	public void sourceNameChanged(DataSourceFactoryEvent e);
}
