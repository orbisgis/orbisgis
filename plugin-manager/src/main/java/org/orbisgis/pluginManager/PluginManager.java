package org.orbisgis.pluginManager;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.orbisgis.pluginManager.background.LongProcess;
import org.orbisgis.pluginManager.background.ProgressDialog;
import org.orbisgis.pluginManager.background.RunnableLongProcess;

public class PluginManager {
	private static Logger logger = Logger.getLogger(PluginManager.class);

	private static ProgressDialog dlg = new ProgressDialog();

	private static PluginManager pluginManager = null;

	private static ArrayList<ErrorListener> listeners = new ArrayList<ErrorListener>();

	private static boolean testing = false;

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
		if (testing) {
			lp.run(dlg);
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					dlg.setVisible(true);
				}

			});
			Thread t = new Thread(new RunnableLongProcess(dlg, dlg, lp));
			t.start();
		}
	}

	public static String getLogFile() {
		return new File(System.getProperty("user.home")
				+ "/OrbisGIS/orbisgis.log").getAbsolutePath();
	}

	public static void addMessageListener(ErrorListener listener) {
		listeners.add(listener);
	}

	public static void removeMessageListener(ErrorListener listener) {
		listeners.remove(listener);
	}

	public static void error(String userMsg, Throwable exception) {
		try {
			logger.error("error", exception);
			String userMessage = getUserMessage(userMsg, exception);
			for (ErrorListener listener : listeners) {
				listener.error(userMessage, exception);
			}
		} catch (Throwable t) {
			logger.error("Error while managing exception", t);
		}
	}

	private static String getUserMessage(String userMsg, Throwable exception) {
		String ret = userMsg;
		ret = ret + ": " + exception.getMessage();
		while (exception.getCause() != null) {
			exception = exception.getCause();
			ret = ret + ":\n" + exception.getMessage();
		}

		return ret;
	}

	public static void warning(String userMsg, Throwable exception) {
		try {
			logger.warn("warning", exception);
			String userMessage = getUserMessage(userMsg, exception);
			for (ErrorListener listener : listeners) {
				listener.warning(userMessage, exception);
			}
		} catch (Throwable t) {
			logger.error("Error while managing exception", t);
		}
	}

	public static void setTesting(boolean debug) {
		PluginManager.testing = debug;
	}
}
