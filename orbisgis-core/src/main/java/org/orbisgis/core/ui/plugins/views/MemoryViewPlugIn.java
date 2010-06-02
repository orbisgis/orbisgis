package org.orbisgis.core.ui.plugins.views;

import javax.swing.JMenuItem;

import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;

public class MemoryViewPlugIn extends ViewPlugIn {

	private ViewPanel panel;
	private JMenuItem menuItem;

	public MemoryViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new ViewPanel();
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.MEMORY, true,
				getIcon(IconNames.MEMORY_ICON), null, panel,context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public boolean isEnabled() {		
		return true;
	}
	
	public boolean isSelected() {
		boolean isSelected = false;
		isSelected = getPlugInContext().viewIsOpen(getId());
		menuItem.setSelected(isSelected);
		return isSelected;
	}
	
	public String getName() {		
		return "Memory view";
	}

}
