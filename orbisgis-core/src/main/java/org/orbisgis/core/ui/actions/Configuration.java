package org.orbisgis.core.ui.actions;

import org.orbisgis.core.ui.action.IAction;
import org.orbisgis.core.ui.configuration.ui.ConfigurationPanel;
import org.orbisgis.sif.UIFactory;

public class Configuration implements IAction {

	@Override
	public void actionPerformed() {
		ConfigurationPanel config = new ConfigurationPanel();
		if (UIFactory.showDialog(config)) {
			config.applyConfigurations();
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
