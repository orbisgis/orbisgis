package org.orbisgis.core.ui.plugins.toc;


import org.orbisgis.core.images.IconNames;

import org.orbisgis.core.renderer.se.common.MapEnv;

import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;

import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class SwitchToDraftPlugIn extends AbstractPlugIn {

	private boolean draft = false;

	@Override
	public boolean execute(PlugInContext context) {
		if (draft) {
			MapEnv.switchToDefault();
		} else {
			MapEnv.switchToDraft();
		}
		draft = !draft;
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();

		context.getFeatureInstaller().addPopupMenuItem(frame, this,
			new String[]{"toogle rendering mode"},
			Names.POPUP_TOC_LEGEND_GROUP, false,
			getIcon(IconNames.POPUP_TOC_LEGEND_ICON), wbContext);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isSelected() {
		return false;
	}
}
