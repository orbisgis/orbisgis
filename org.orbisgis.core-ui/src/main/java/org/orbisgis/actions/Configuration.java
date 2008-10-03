package org.orbisgis.actions;

import org.orbisgis.action.IAction;
import org.orbisgis.configuration.ConfigurationPanel;
import org.sif.UIFactory;

public class Configuration implements IAction {

	@Override
	public void actionPerformed() {
		ConfigurationPanel config = new ConfigurationPanel();
		if (UIFactory.showDialog(config)) {
			config.savePreferences();
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}
}
