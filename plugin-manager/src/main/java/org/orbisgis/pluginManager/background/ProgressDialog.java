/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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
package org.orbisgis.pluginManager.background;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.orbisgis.IProgressMonitor;
import org.orbisgis.ProgressMonitor;

public class ProgressDialog extends JDialog implements IProgressMonitor {

	private static Logger logger = Logger.getLogger(ProgressDialog.class);

	private JLabel lbl;

	private IProgressMonitor pm;

	private JProgressBar progressBar;

	private int counter = 0;

	public ProgressDialog() {
		Container c = getContentPane();
		progressBar = new JProgressBar(0, 100);
		c.add(progressBar, BorderLayout.CENTER);
		lbl = new JLabel("");
		c.add(lbl, BorderLayout.NORTH);
		setModal(true);
		setUndecorated(false);
		setLocationRelativeTo(null);
		setResizable(false);
		setFocusable(false);
		setFocusableWindowState(false);

	}

	public synchronized void startProcess(LongProcess process) {
		lbl.setText(process.getTaskName());
		pm = new ProgressMonitor(process.getTaskName());
		this.pack();
		progressBar.setValue(0);
		RunnableLongProcess runnable = new RunnableLongProcess(this, process);
		Thread t = new Thread(runnable);
		t.start();
	}

	public void endTask() {
		pm.endTask();
	}

	public int getProgress() {
		return pm.getProgress();
	}

	public void init(String taskName) {
		pm.init(taskName);
	}

	public void progressTo(int progress) {
		pm.progressTo(progress);
		progressBar.setValue(pm.getProgress());
	}

	public void startTask(String taskName, int percentage) {
		pm.startTask(taskName, percentage);
	}

	public void setVisible(final boolean visible) {
		if (SwingUtilities.isEventDispatchThread()) {
			synchronized (this) {
				logger.debug("visibility to: " + visible + " with count: "
						+ counter);
				if (visible) {
					counter++;
				} else {
					counter--;
				}
			}
			super.setVisible(counter > 0);
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					setVisible(visible);
				}

			});
		}
	}
}
