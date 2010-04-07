package org.orbisgis.core.ui.plugins.views;

import java.awt.Component;
import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.geocognition.GeocognitionView;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;

public class GeocognitionViewPlugIn extends ViewPlugIn {

	private GeocognitionView panel;
	private JMenuItem menuItem;

	public GeocognitionView getPanel() {
		return panel;
	}

	public void delete() {
		panel.delete();
	}

	public Component getComponent() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new GeocognitionView();
		panel.initialize();
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.GEOCOGNITION, true,
				getIcon(IconNames.GEOCOGNITION_ICON), null, panel, context);
	}
	
	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public void update(Observable o, Object arg) {
		menuItem.setSelected(getPlugInContext().viewIsOpen(getId()));
	}	

	public void loadStatus() throws PersistenceException {
		panel.loadStatus();
	}

	public void saveStatus() throws PersistenceException {
		panel.saveStatus();
	}
	
	public String getName() {		
		return "Geocognition view";
	}
}
