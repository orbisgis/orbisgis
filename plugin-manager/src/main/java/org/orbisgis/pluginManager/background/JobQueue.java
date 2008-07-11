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
package org.orbisgis.pluginManager.background;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class JobQueue implements BackgroundManager {

	private static Logger logger = Logger.getLogger(JobQueue.class);

	private ArrayList<BackgroundListener> listeners = new ArrayList<BackgroundListener>();

	private ArrayList<Job> queue = new ArrayList<Job>();
	private Job current;

	private ProgressDialog dlg = new ProgressDialog();

	public synchronized void add(JobId processId, BackgroundJob lp,
			boolean blocking) {
		logger.info("Adding a job: " + processId);
		Job newJob = new Job(processId, lp, this, blocking);
		// Check if it's the current process
		if ((current != null) && (current.getId().is(processId))) {
			current.cancel();
			queue.add(0, newJob);
			fireJobAdded(newJob);
			newJob.progressTo(0);
			// we don't planify because we will do it when the cancelled process
			// ends
		} else {
			// Substitute existing process
			for (Job job : queue) {
				if (job.getId().is(processId)) {
					logger.info("Substituting previous job: " + processId);
					job.setProcess(lp);
					fireJobReplaced(job);
					return;
				}
			}

			// Add a new one
			logger.info("It's a new job: " + processId);
			queue.add(newJob);
			fireJobAdded(newJob);
			newJob.progressTo(0);

			planify();
		}
	}

	private synchronized void planify() {
		if (current == null && queue.size() > 0) {
			current = queue.remove(0);
			logger.info("Starting job: " + current.getId());

			if (current.isBlocking()) {
				dlg.setJob(current);
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						logger.info("Showing dialog for job: "
								+ current.getId());
						dlg.setVisible(true);
					}

				});
			} else {
				current.start();
			}
		}
	}

	public void add(BackgroundJob lp, boolean blocking) {
		add(new UniqueJobID(), lp, blocking);
	}

	public synchronized void processFinished(JobId processId) {
		logger.info("Job finished: " + processId);
		Job finishedJob = current;
		finishedJob.clear();
		current = null;
		if (dlg.isVisible()) {
			dlg.jobFinished();
		}
		planify();
		fireJobRemoved(finishedJob);
	}

	public synchronized Job[] getJobs() {
		Job[] jobs = queue.toArray(new Job[0]);
		if (current == null) {
			return jobs;
		} else {
			Job[] ret = new Job[jobs.length + 1];
			ret[0] = current;
			System.arraycopy(jobs, 0, ret, 1, jobs.length);

			return ret;
		}
	}

	public void backgroundOperation(BackgroundJob lp) {
		add(lp, true);
	}

	public void backgroundOperation(JobId processId, BackgroundJob lp) {
		add(processId, lp, true);
	}

	public void addBackgroundListener(BackgroundListener listener) {
		listeners.add(listener);
	}

	public void removeBackgroundListener(BackgroundListener listener) {
		listeners.remove(listener);
	}

	private void fireJobReplaced(Job job) {
		for (BackgroundListener listener : listeners) {
			listener.jobReplaced(job);
		}
	}

	private void fireJobRemoved(Job job) {
		for (BackgroundListener listener : listeners) {
			listener.jobRemoved(job);
		}
	}

	private void fireJobAdded(Job job) {
		for (BackgroundListener listener : listeners) {
			listener.jobAdded(job);
		}
	}

	public JobQueue getJobQueue() {
		return this;
	}

	public void nonBlockingBackgroundOperation(BackgroundJob lp) {
		add(lp, false);
	}

	public void nonBlockingBackgroundOperation(JobId processId,
			BackgroundJob lp) {
		add(processId, lp, false);
	}

}
