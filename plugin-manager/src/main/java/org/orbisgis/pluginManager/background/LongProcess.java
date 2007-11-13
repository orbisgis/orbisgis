package org.orbisgis.pluginManager.background;

import org.orbisgis.IProgressMonitor;

public interface LongProcess {

	void run(IProgressMonitor pm);

	String getTaskName();

}
