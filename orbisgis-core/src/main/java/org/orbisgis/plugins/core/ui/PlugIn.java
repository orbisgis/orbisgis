package org.orbisgis.plugins.core.ui;

import java.util.Observer;

public interface PlugIn extends Observer {
	// Implemented by all PlugIn
	void initialize(PlugInContext context) throws Exception;

	boolean execute(PlugInContext context) throws Exception;

	boolean isVisible();
	
	void i18nConfigure(String lang, String country);
}
