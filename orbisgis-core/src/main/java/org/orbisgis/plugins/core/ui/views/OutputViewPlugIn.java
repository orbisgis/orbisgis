package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.workbench.Names;

public class OutputViewPlugIn extends ViewPlugIn {

	private OutputPanel panel;
	private JMenuItem menuItem;

	public OutputViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new OutputPanel();
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.OUTPUT, true,
				getIcon(Names.OUTPUT_ICON), null, panel, null, null,
				context.getWorkbenchContext());
		Services.registerService(OutputManager.class,
				"Service to send messages to the output system", panel);
	}

	@Override
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
