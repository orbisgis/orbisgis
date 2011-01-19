/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT, Adelin PIAU
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 * adelin.piau _at_ ec-nantes.fr
 */

package org.orbisgis.core.ui.components.job;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.background.Job;
import org.orbisgis.core.background.JobId;
import org.orbisgis.core.background.JobQueue;
import org.orbisgis.core.background.ProgressBar;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.windows.mainFrame.OrbisGISFrame;

/**
 * Job popup at bootom right to follow processes loading
 */

public class JobWindow extends JPanel {
	
	private static Logger logger = Logger.getLogger(JobQueue.class);

	private JLayeredPane jobPopupLayeredPane;
	private JScrollPane progressPopup;
	private JPanel progressPanel;
	private HashMap<JobId, Component[]> idBar = new HashMap<JobId, Component[]>();

	public JobWindow(final OrbisGISFrame orbisGISFrame) {
		jobPopupLayeredPane = orbisGISFrame.getLayeredPane();
	}

	public void show() {
		if (progressPopup == null) {
			initUI();
		}

		progressPopup.setLocation(Services.getService(WorkbenchContext.class)
				.getWorkbench().getFrame().getSize().width - 208, 25);
		jobPopupLayeredPane.add(progressPopup, 0);
		
	}

	public void hide() {
		if (progressPopup != null) {
			progressPopup.setVisible(false);
		}
		jobPopupLayeredPane.remove(progressPopup);
	}

	public void initUI() {
		progressPanel = new JPanel();
		progressPanel.setLayout(new CRFlowLayout());
		progressPopup = new JScrollPane(progressPanel);
		progressPopup.setSize(200, 80);	
	}

	public void addJob(Job job) {
		progressPanel.removeAll();
		Job[] jobs = getBackgroundManager().getJobQueue().getJobs();
		for (Job queuedJob : jobs) {
			Component[] comps = idBar.get(queuedJob.getId());
			if (comps != null) {
				((ProgressBar) comps[0]).setJob(queuedJob);
				for (Component component : comps) {
					progressPanel.add(component);
				}
			} else {
				ProgressBar bar = new ProgressBar(job);
				progressPanel.add(bar);
				CarriageReturn cr = new CarriageReturn();
				progressPanel.add(cr);
				idBar.put(job.getId(), new Component[] { bar, cr });
			}
		}
		
		logger.info("Added job " + job.getId());
		progressPopup.setVisible(true);
	}

	private BackgroundManager getBackgroundManager() {
		return (BackgroundManager) Services.getService(BackgroundManager.class);
	}

	public void removeJob(Job job) {
		Job[] jobs = getBackgroundManager().getJobQueue().getJobs();
		if (jobs.length == 0 || !job.getId().is(jobs[0].getId())) {
			Component[] comps = idBar.remove(job.getId());
			if (comps != null) {
				for (Component component : comps) {
					progressPanel.remove(component);
				}
			}
			invalidate();
			repaint(0, 0, getWidth(), getHeight());
			progressPanel.invalidate();
			progressPanel.doLayout();
			logger.info("Removed job " + job.getId());
		}
		if (idBar.isEmpty())
			hide();
	}

	public void replaceJob(Job job) {
		Component[] comps = idBar.get(job.getId());
		ProgressBar bar = (ProgressBar) comps[0];
		bar.setJob(job);
		logger.info("Replaced job " + job.getId());
	}
}
