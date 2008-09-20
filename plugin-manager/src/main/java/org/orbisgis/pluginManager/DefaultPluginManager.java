/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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

import org.orbisgis.Services;
import org.orbisgis.errorManager.DefaultErrorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.background.JobQueue;
import org.orbisgis.pluginManager.workspace.DefaultWorkspace;
import org.orbisgis.workspace.Workspace;

public class DefaultPluginManager implements PluginManager {

	private static ArrayList<SystemListener> listeners = new ArrayList<SystemListener>();

	private ArrayList<Plugin> plugins;

	public void setPlugins(ArrayList<Plugin> plugins) {
		this.plugins = plugins;
	}

	void start() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {

			public void run() {
				try {
					startPlugins();
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
				Services.getErrorManager().error("Error starting plug-in", e);
			}
		}
	}

	public void stop() {
		if (stopPlugins()) {
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
				Services.getErrorManager().error("Error stoping plug-in", e);
			}
		}

		return true;
	}

	public String getLogFile() {
		return new File(System.getProperty("user.home")
				+ "/OrbisGIS/orbisgis.log").getAbsolutePath();
	}

	public File getHomeFolder() {
		return new File(System.getProperty("user.home") + "/OrbisGIS/");
	}

	public void addSystemListener(SystemListener listener) {
		listeners.add(listener);
	}

	public void removeSystemListener(SystemListener listener) {
		listeners.remove(listener);
	}

	@SuppressWarnings("unchecked")
	public static void fireEvent() {
		ArrayList<SystemListener> ls = (ArrayList<SystemListener>) listeners
				.clone();
		for (SystemListener listener : ls) {
			listener.statusChanged();
		}
	}

	public void installServices() {
		Services
				.registerService(
						BackgroundManager.class,
						"Execute tasks in background processes, "
								+ "showing progress bars. Gives access to the job queue",
						new JobQueue());

		Services.registerService(Workspace.class,
				"Change workspace, save files in the workspace, etc.",
				new DefaultWorkspace());

		Services.registerService(ErrorManager.class,
				"Notification of errors to the system",
				new DefaultErrorManager());

		Services
				.registerService(
						PluginManager.class,
						"Gives access to the plug-in system manager. It can stop the application, access the log files, etc.",
						this);
	}
}
