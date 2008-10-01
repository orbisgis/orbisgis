package org.orbisgis.actions;

import org.orbisgis.action.IAction;
import org.orbisgis.config.ConfigPanel;
import org.sif.UIFactory;

public class Preferences implements IAction {

	@Override
	public void actionPerformed() {
		ConfigPanel config = new ConfigPanel();
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
