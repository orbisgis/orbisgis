package org.gdms.source;

import org.gdms.driver.DriverException;

/**
 * Interface implemented for those decorators that have to keep their content up
 * to date when a commit is done
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface CommitListener {

	/**
	 * The specified source is going to be changed
	 *
	 * @param name
	 *            name of the source
	 * @throws DriverException
	 *             If the commit should be cancelled
	 */
	void isCommiting(String name, Object source) throws DriverException;

	/**
	 * The specified source has been modified
	 *
	 * @param name
	 *            name of the source
	 * @throws DriverException
	 *             If some decorator could not be updated to the new contents
	 */
	void commitDone(String name) throws DriverException;

	/**
	 * Gets the name of the source this commit listener represents
	 *
	 * @return
	 */
	String getName();
}
