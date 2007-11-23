package org.orbisgis.pluginManager;

public interface SystemListener {

	public void error(String userMsg, Throwable exception);

	public void warning(String userMsg, Throwable e);

	public void statusChanged();
}
