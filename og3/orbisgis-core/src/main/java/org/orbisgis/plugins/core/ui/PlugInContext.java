package org.orbisgis.plugins.core.ui;

import org.orbisgis.plugins.core.ui.workbench.FeatureInstaller;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public class PlugInContext {
	private WorkbenchContext workbenchContext;
	private FeatureInstaller featureInstaller;

	public PlugInContext(WorkbenchContext workbenchContext) {
		this.workbenchContext = workbenchContext;
		featureInstaller = new FeatureInstaller(workbenchContext);
	}

	public WorkbenchContext getWorkbenchContext() {
		return workbenchContext;
	}

	public FeatureInstaller getFeatureInstaller() {
		return featureInstaller;
	}
}
