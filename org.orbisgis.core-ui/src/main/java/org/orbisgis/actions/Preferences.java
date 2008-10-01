package org.orbisgis.actions;

import org.orbisgis.action.IAction;
import org.orbisgis.config.ConfigDialog;
import org.sif.UIFactory;

public class Preferences implements IAction {

	@Override
	public void actionPerformed() {
		ConfigDialog config = new ConfigDialog();
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
