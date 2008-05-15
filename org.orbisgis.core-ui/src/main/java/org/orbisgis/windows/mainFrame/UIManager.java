package org.orbisgis.windows.mainFrame;

import javax.swing.JFrame;

import org.orbisgis.Services;

public interface UIManager {

	/**
	 * Gets the main frame of the application. Useful to set the parent of the
	 * dialogs. There is no functionality in this frame that cannot be used
	 * through the preferred way, the {@link Services} class
	 *
	 * @return
	 */
	JFrame getMainFrame();

	/**
	 * Refreshes the status of the actions. Usually this method is invoked
	 * automatically but there are certain cases where it is necessary to call
	 * it manually
	 */
	void refreshUI();

}
