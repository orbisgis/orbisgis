package org.orbisgis.core.windows;

import java.awt.Rectangle;

import org.orbisgis.core.persistence.PersistenceException;

public interface IWindow {

	void showWindow();

	/**
	 * Saves this instance permanently. If the content is going to be stored in
	 * a file, this file should be obtained from the PersistenceContext instance
	 *
	 * @param pc
	 * @throws PersistenceException
	 */
	void save(PersistenceContext pc) throws PersistenceException;

	/**
	 * Loads the previously stored status. If the status was stored in files they
	 * can be retrieved by the PersistenceContext
	 *
	 * @param pc
	 * @throws PersistenceException
	 */
	void load(PersistenceContext pc) throws PersistenceException;

	Rectangle getPosition();

	void setPosition(Rectangle position);

	boolean isOpened();

}
