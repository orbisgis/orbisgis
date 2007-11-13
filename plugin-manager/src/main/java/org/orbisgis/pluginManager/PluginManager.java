package org.orbisgis.pluginManager;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.orbisgis.pluginManager.background.LongProcess;
import org.orbisgis.pluginManager.background.ProgressDialog;
import org.orbisgis.pluginManager.background.RunnableLongProcess;

public class PluginManager {

	private static ProgressDialog dlg = new ProgressDialog();

	private static PluginManager pluginManager = null;

	private ArrayList<Plugin> plugins;

	public PluginManager(ArrayList<Plugin> plugins) {
		this.plugins = plugins;
	}

	public static void createPluginManager(ArrayList<Plugin> plugins) {
		pluginManager = new PluginManager(plugins);
	}

	static void start() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {

			public void run() {
				try {
					pluginManager.startPlugins();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		});
	}

	private void startPlugins() throws Exception {
		for (int i = 0; i < plugins.size(); i++) {
			plugins.get(i).start();
		}
	}

	public static void stop() {
		pluginManager.stopPlugins();
		System.exit(0);
	}

	private void stopPlugins() {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				plugins.get(i).stop();
			} catch (Exception e) {
				// TODO Notify error manager.
			}
		}
	}

	public static void backgroundOperation(LongProcess lp) {
		dlg.setText(lp.getTaskName());
		dlg.pack();
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				synchronized (PluginManager.class){
					dlg.setVisible(true);
				}
			}

		});
		Thread t = new Thread(new RunnableLongProcess(dlg, dlg, lp));
		t.start();
	}

}
