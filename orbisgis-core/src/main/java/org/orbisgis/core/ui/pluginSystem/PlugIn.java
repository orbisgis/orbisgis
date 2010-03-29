package org.orbisgis.core.ui.pluginSystem;

import java.util.Observer;

public interface PlugIn extends Observer {
	// Implemented by all PlugIn
	void initialize(PlugInContext context) throws Exception;

	boolean execute(PlugInContext context) throws Exception;

	boolean isVisible();
	
	boolean isSelected();
	
	void getI18n();
	
	String getName();
	
	public void setPlugInContext(PlugInContext plugInContext);
}
