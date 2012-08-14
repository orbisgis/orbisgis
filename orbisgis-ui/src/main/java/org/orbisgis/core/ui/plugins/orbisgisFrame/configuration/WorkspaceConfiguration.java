/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;

import org.orbisgis.core.Services;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.utils.I18N;

public class WorkspaceConfiguration implements IConfiguration {

	private WorkspacePanel panel;
	private static final String TIMER_PROPERTY = "timer";
	private String timerValue = "1";

	@Override
	public void applyUserInput() {
		if (panel.getAuthCheck().isSelected())
			timerValue = panel.getTimer().getValue();
		else
			timerValue = null;
		apply(timerValue);
		if (timerValue != null)
			changePeriodicSaving(timerValue);
	}

	private void apply(String timerValue) {
		Properties systemSettings = System.getProperties();
		if (timerValue != null)
			systemSettings.put(TIMER_PROPERTY, timerValue);
		else
			systemSettings.remove(TIMER_PROPERTY);
		System.setProperties(systemSettings);
	}

	@Override
	public JComponent getComponent() {
		stopPeriodicSaving();
		panel = new WorkspacePanel(timerValue);
		panel.init();
		return panel;
	}

	@Override
	public void loadAndApply() {
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		timerValue = bc.getProperty(TIMER_PROPERTY);
		apply(timerValue);
	}

	@Override
	public void saveApplied() {
		Properties systemSettings = System.getProperties();
		timerValue = systemSettings.getProperty(TIMER_PROPERTY);
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		if (timerValue != null)
			bc.setProperty(TIMER_PROPERTY, timerValue);
		else {
			bc.removeProperty(TIMER_PROPERTY);
		}

	}

	private static void changePeriodicSaving(String sTimer) {
		stopPeriodicSaving();
		int iTimer;
		if ((iTimer = convert(sTimer)) != 0)
			startPeriodicSaving(iTimer);
	}

	private static void stopPeriodicSaving() {
		DefaultWorkspace workspace = (DefaultWorkspace) Services
				.getService(Workspace.class);
		workspace.getTimer().stopSaving();
	}

	public static int convert(String sTimer) {
		try {
			return Integer.parseInt(sTimer);
		} catch (NumberFormatException e) {
			Services
					.getErrorManager()
					.warning(
							I18N
									.getString("orbisgis.org.orbisgis.configuration.workspace.noTimer"));
		}
		return 0;
	}

	public static void startPeriodicSaving(int timer) {
		DefaultWorkspace workspace = (DefaultWorkspace) Services
				.getService(Workspace.class);
		// Apply periodic saving
		workspace.setTimer(new PeriodicSaveWorkspace(workspace));
		workspace.getTimer().setPeriodicTimeToSaveWrksp(
				TimeUnit.MINUTES.toMillis(timer));
		workspace.getTimer().start();
	}

	public static String getTimerProperty() {
		return TIMER_PROPERTY;
	}

	public String validateInput() {
		if (panel.getAuthCheck().isSelected()) {
			String timer = panel.getTimer().getValue();
			if (timer.equals("")) {
				return I18N
						.getString("orbisgis.org.orbisgis.configuration.workspace.timerGreaterThan1");
			} else {
				try {
					int timerValue = Integer.parseInt(timer);
					if (timerValue < 1 && timerValue > 60) {
						return I18N
								.getString("orbisgis.org.orbisgis.configuration.workspace.timerBetween1and60");
					}
				} catch (NumberFormatException e) {
					return I18N
							.getString("orbisgis.org.orbisgis.configuration.workspace.onlyNumericAllowed");
				}

			}

		}
		return null;
	}
}
