package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.views.information.InformationManager;
import org.orbisgis.plugins.core.ui.views.information.Table;
import org.orbisgis.plugins.core.ui.workbench.Names;

public class InformationViewPlugIn extends ViewPlugIn {

	private Table panel;
	private JMenuItem menuItem;

	public void initialize(PlugInContext context) throws Exception {
		panel = new Table();
		Services.registerService(InformationManager.class,
				"Service to show tabular information to the user.", panel);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.INFORMATION, true,
				getIcon(Names.INFORMATION_ICON), null, panel, null, null,
				context.getWorkbenchContext());
	}

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}

	public void update(Observable o, Object arg) {
		setSelected();
	}

	public void setSelected() {
		menuItem.setSelected(isVisible());
	}

	public boolean isVisible() {
		return getUpdateFactory().viewIsOpen(getId());
	}

}
