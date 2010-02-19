package org.orbisgis.core.ui.pluginSystem.workbench;

//WorkbenchContext
public class OrbisWorkbenchContext extends WorkbenchContext {

	private OrbisWorkbench workbench;

	public OrbisWorkbenchContext(OrbisWorkbench orbisWorkbench) {
		this.workbench = orbisWorkbench;
	}

	public OrbisWorkbench getWorkbench() {
		return workbench;
	}
}
