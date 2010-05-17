package org.orbisgis.core.ui.pluginSystem;

import java.util.Observer;

public interface PlugIn extends Observer {
	// Implemented by all PlugIn
	public void initialize(PlugInContext context) throws Exception;

	boolean execute(PlugInContext context) throws Exception;

	boolean isEnabled();
	
	boolean isSelected();
	
	String getName();
}
