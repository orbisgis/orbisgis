package org.orbisgis.pluginManager.background;

import javax.swing.JDialog;

import org.orbisgis.IProgressMonitor;
import org.orbisgis.pluginManager.PluginManager;

public class RunnableLongProcess implements Runnable {

	private JDialog dlg;
	private LongProcess lp;
	private IProgressMonitor pm;

	public RunnableLongProcess(IProgressMonitor pm, JDialog dlg, LongProcess lp) {
		this.dlg = dlg;
		this.lp = lp;
		this.pm = pm;
	}

	public void run() {
		try {
			lp.run(pm);
		} catch (Throwable t) {
			PluginManager.error(t.getMessage(), t);
		} finally {
			dlg.setVisible(false);
		}
	}

}
