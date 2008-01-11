/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.pluginManager;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.orbisgis.ProgressMonitor;
import org.orbisgis.pluginManager.background.LongProcess;
import org.orbisgis.pluginManager.background.ProgressDialog;
import org.orbisgis.pluginManager.workspace.Workspace;

public class PluginManager {
	private static Logger logger = Logger.getLogger(PluginManager.class);

	private static PluginManager pluginManager = null;

	private static ProgressDialog dlg = new ProgressDialog();

	private static ArrayList<SystemListener> listeners = new ArrayList<SystemListener>();

	private static boolean testing = false;

	private static Workspace workspace = new Workspace();

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
			try {
				plugins.get(i).start();
			} catch (Exception e) {
				error("Error starting plug-in", e);
			}
		}
	}

	public static void stop() {
		if (pluginManager.stopPlugins()) {
			System.exit(0);
		}
	}

	private boolean stopPlugins() {
		for (int i = 0; i < plugins.size(); i++) {
			if (!plugins.get(i).allowStop()) {
				return false;
			}
		}
		for (int i = 0; i < plugins.size(); i++) {
			try {
				plugins.get(i).stop();
			} catch (Exception e) {
				PluginManager.error("Error stoping plug-in", e);
			}
		}

		return true;
	}

	public static void backgroundOperation(LongProcess lp) {
		if (testing) {
			lp.run(new ProgressMonitor(lp.getTaskName()));
		} else {
			dlg.startProcess(lp);
		}
	}

	public static String getLogFile() {
		return new File(System.getProperty("user.home")
				+ "/OrbisGIS/orbisgis.log").getAbsolutePath();
	}

	public static File getHomeFolder() {
		return new File(System.getProperty("user.home") + "/OrbisGIS/");
	}

	public static void addSystemListener(SystemListener listener) {
		listeners.add(listener);
	}

	public static void removeSystemListener(SystemListener listener) {
		listeners.remove(listener);
	}

	public static void error(String userMsg, Throwable exception) {
		try {
			logger.error(userMsg, exception);
			String userMessage = getUserMessage(userMsg, exception);
			for (SystemListener listener : listeners) {
				listener.error(userMessage, exception);
			}
		} catch (Throwable t) {
			logger.error("Error while managing exception", t);
		}
	}

	private static String getUserMessage(String userMsg, Throwable exception) {
		String ret = userMsg;
		if (exception != null) {
			ret = ret + ": " + exception.getMessage();
			while (exception.getCause() != null) {
				exception = exception.getCause();
				ret = ret + ":\n" + exception.getMessage();
			}
		}

		return ret;
	}

	public static void warning(String userMsg, Throwable exception) {
		try {
			logger.warn("warning: " + userMsg, exception);
			String userMessage = getUserMessage(userMsg, exception);
			for (SystemListener listener : listeners) {
				listener.warning(userMessage, exception);
			}
		} catch (Throwable t) {
			logger.error("Error while managing exception", t);
		}
	}

	public static void setTesting(boolean debug) {
		PluginManager.testing = debug;
	}

	public static void fireEvent() {
		for (SystemListener listener : listeners) {
			listener.statusChanged();
		}
	}

	public static Workspace getWorkspace() {
		return workspace;
	}

	public static void error(String userMsg) {
		error(userMsg, null);
	}
}
