package org.orbisgis.pluginManager;

import org.orbisgis.pluginManager.background.Job;

public abstract class SystemAdapter implements SystemListener {

	public void error(String userMsg, Throwable exception) {

	}

	public void jobAdded(Job job) {

	}

	public void jobRemoved(Job job) {

	}

	public void jobReplaced(Job job) {

	}

	public void statusChanged() {

	}

	public void warning(String userMsg, Throwable e) {

	}

}
