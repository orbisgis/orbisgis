package org.orbisgis.core.windows;

import java.awt.Rectangle;
import java.io.File;

import org.orbisgis.core.persistence.PersistenceException;

public interface IWindow {

	void showWindow();

	void save(File file) throws PersistenceException;

	void load(File file) throws PersistenceException;

	Rectangle getPosition();

	void setPosition(Rectangle position);

	boolean isOpened();

}
