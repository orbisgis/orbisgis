package org.orbisgis.actions;

import javax.swing.JOptionPane;

import org.orbisgis.Services;
import org.orbisgis.action.IAction;
import org.orbisgis.pluginManager.PluginManager;

public class Exit implements IAction {

	public void actionPerformed() {
		int answer = JOptionPane.showConfirmDialog(null, "Really quit?",
				"OrbisGIS", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			PluginManager psm = (PluginManager) Services
					.getService("org.orbisgis.PluginManager");
			psm.stop();
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}

}
