package org.orbisgis.pluginManager;

public interface ErrorListener {

	public void error(String userMsg, Throwable exception);

	public void warning(String userMsg, Throwable e);
}
