package org.gdms.data;

public interface DataSourceListener {

	/**
	 * Invoked when this DataSource is effectively opened. Note that if open is
	 * called twice consecutively the second time is not an effective opening.
	 *
	 * @param ds
	 *            DataSource that has been opened
	 */
	void open(DataSource ds);

	/**
	 * Invoked when this DataSource is effectively closed. Note that if open is
	 * called twice the first call to cancel is not an effective close.
	 *
	 * @param ds
	 *            DataSource that have been closed
	 */
	void cancel(DataSource ds);

	/**
	 * Invoked when this DataSource is effectively saved and closed. Note that
	 * if open is called twice consecutively the second time is not an effective
	 * opening.
	 *
	 * @param ds
	 *            DataSource that have been commited
	 */
	void commit(DataSource ds);

}
