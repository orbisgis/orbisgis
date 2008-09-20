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
package org.orbisgis.views.jobs;

import java.awt.Component;

import javax.swing.SwingUtilities;

import org.orbisgis.Services;
import org.orbisgis.pluginManager.background.BackgroundListener;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.background.Job;
import org.orbisgis.view.IView;

public class JobView implements IView {

	private JobPanel processPanel = new JobPanel();

	public void delete() {
	}

	public Component getComponent() {
		return processPanel;
	}

	public void initialize() {
		BackgroundManager bm = (BackgroundManager) Services
				.getService(BackgroundManager.class);

		bm.addBackgroundListener(new BackgroundListener() {

			public void jobAdded(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					processPanel.addJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							processPanel.addJob(job);
						}

					});
				}
			}

			public void jobRemoved(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					processPanel.removeJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							processPanel.removeJob(job);
						}

					});
				}
			}

			public void jobReplaced(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					processPanel.replaceJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							processPanel.replaceJob(job);
						}

					});
				}
			}
		});
	}

	public void loadStatus() {
	}

	public void saveStatus() {
	}

}
