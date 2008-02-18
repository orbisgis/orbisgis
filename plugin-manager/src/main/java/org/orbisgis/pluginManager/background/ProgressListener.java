package org.orbisgis.pluginManager.background;

public interface ProgressListener {

	void progressChanged(Job job);

	void subTaskStarted(Job job);

	void subTaskFinished(Job job);

}
