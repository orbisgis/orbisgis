package org.orbisgis.pluginManager.background;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.orbisgis.pluginManager.PluginManager;

public class RunnableLongProcess implements Runnable {

	private LongProcess lp;
	private ProgressDialog pm;

	public RunnableLongProcess(ProgressDialog pm, LongProcess lp) {
		this.lp = lp;
		this.pm = pm;
	}

	public void run() {
		try {
			Timer t = new Timer(1000, new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							pm.setVisible(true);
						}

					});
				}

			});
			t.setRepeats(false);
			t.start();
			lp.run(pm);
			PluginManager.fireEvent();
		} catch (Throwable t) {
			PluginManager.error(t.getMessage(), t);
		} finally {
			pm.setVisible(false);
		}
	}

}
