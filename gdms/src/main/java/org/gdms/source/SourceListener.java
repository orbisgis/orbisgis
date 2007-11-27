package org.gdms.source;


public interface SourceListener {

	/**
	 * Called when some source is added to the system
	 *
	 * @param e
	 */
	public void sourceAdded(SourceEvent e);

	/**
	 * Called when some source is removed
	 *
	 * @param e
	 */
	public void sourceRemoved(SourceRemovalEvent e);

	/**
	 * Called when the name of a datasource is changed
	 *
	 * @param e
	 */
	public void sourceNameChanged(SourceEvent e);

}
