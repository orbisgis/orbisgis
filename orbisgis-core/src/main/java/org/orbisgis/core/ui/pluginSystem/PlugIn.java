package org.orbisgis.core.ui.pluginSystem;

import java.util.Observer;

import org.orbisgis.core.language.I18N;

public interface PlugIn extends Observer {
	// Implemented by all PlugIn
	void initialize(PlugInContext context) throws Exception;

	boolean execute(PlugInContext context) throws Exception;

	boolean isVisible();
	
	boolean isSelected();
	
	I18N getI18n();
	
	String getName();
	
	public void setPlugInContext(PlugInContext plugInContext);
}
