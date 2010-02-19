package org.orbisgis.plugins.core.ui.workbench;

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
