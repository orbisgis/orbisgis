package org.orbisgis.core.ui.plugins.views;

import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.information.InformationManager;
import org.orbisgis.core.ui.plugins.views.information.Table;

public class InformationViewPlugIn extends ViewPlugIn {

	private Table panel;
	private JMenuItem menuItem;

	public void initialize(PlugInContext context) throws Exception {
		panel = new Table();
		Services.registerService(InformationManager.class,
				"Service to show tabular information to the user.", panel);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.INFORMATION, true,
				getIcon(IconNames.INFORMATION_ICON), null, panel, context);
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
		return "Information view";
	}

}
