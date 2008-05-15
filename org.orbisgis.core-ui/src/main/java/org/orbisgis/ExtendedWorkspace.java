package org.orbisgis;

import java.io.File;

import org.orbisgis.pluginManager.workspace.Workspace;

public interface ExtendedWorkspace extends Workspace {

	/**
	 * Returns the folder inside the workspace where results can be stored
	 *
	 * @return
	 */
	File  getResultsFolder();

	/**
	 * Returns the folder inside the workspace where temporal results can be
	 * stored. At the higher frequency this folder will be removed each time the
	 * application exits
	 *
	 * @return
	 */
	File getTempFolder();
}
