package org.orbisgis.core.ui.plugins.views;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;

public class OutputViewPlugIn extends ViewPlugIn {

	private OutputPanel panel;
	private JMenuItem menuItem;

	public OutputViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new OutputPanel(Names.OUTPUT);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.OUTPUT, true,
				getIcon(IconNames.OUTPUT_ICON), null, panel, context);
		Services.registerService(OutputManager.class,
				"Service to send messages to the output system", panel);
	}
	
	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}
	
	public void update(Observable o, Object arg) {
		menuItem.setSelected(getPlugInContext().viewIsOpen(getId()));
	}
	
	public String getName() {		
		return "Output view";
	}
}
