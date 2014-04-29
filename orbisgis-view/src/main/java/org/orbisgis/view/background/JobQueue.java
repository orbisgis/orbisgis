/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.background;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * Manages blocking and parallel GUI Jobs.
 */
public class JobQueue implements BackgroundManager {

	private static final Logger LOGGER = Logger.getLogger("gui."+JobQueue.class);

	private ArrayList<BackgroundListener> listeners = new ArrayList<BackgroundListener>();

	private ArrayList<Job> queue = new ArrayList<Job>();
	private Job current;

	public synchronized void add(JobId processId, BackgroundJob lp,
			boolean blocking) {
		LOGGER.debug("Adding a job: " + processId);
		Job newJob = new Job(processId, lp, this, blocking);
		// Check if it's the current process
		if ((current != null) && (current.getId().is(processId))) {
			current.cancel();
			queue.add(0, newJob);
			fireJobAdded(newJob);
			// we don't planify because we will do it when the cancelled process
			// ends
		} else {
			// Substitute existing process
			for (Job job : queue) {
				if (job.getId().is(processId)) {
					LOGGER.debug("Substituting previous job: " + processId);
					job.setProcess(lp);
					fireJobReplaced(job);
					return;
				}
			}
			// Add a new one
			LOGGER.debug("It's a new job: " + processId + " " + newJob.getTaskName());
			queue.add(newJob);
			fireJobAdded(newJob);
			planify();
		}
	}

	private synchronized void planify() {
		if (current == null && queue.size() > 0) {
			current = queue.remove(0);
			LOGGER.debug("Starting job: " + current.getId());

			if (current.isBlocking()) {
				SwingUtilities.invokeLater(current.getReadyRunnable());
			} else {
				current.start();
			}
		}
	}

	public JobId add(BackgroundJob lp, boolean blocking) {
                JobId jobId = new UniqueJobID();
		add(jobId, lp, blocking);
                return jobId;
	}

        @Override
	public synchronized void processFinished(JobId processId) {
		LOGGER.debug("Job finished: " + processId);
		Job finishedJob = current;
		finishedJob.clear();
		current = null;
		planify();
		fireJobRemoved(finishedJob);
	}

	public synchronized Job[] getJobs() {
		Job[] jobs = queue.toArray(new Job[queue.size()]);
		if (current == null) {
			return jobs;
		} else {
			Job[] ret = new Job[jobs.length + 1];
			ret[0] = current;
			System.arraycopy(jobs, 0, ret, 1, jobs.length);

			return ret;
		}
	}

        @Override
	public JobId backgroundOperation(BackgroundJob lp) {
		// TODO JOB Open Job window docking and undock that
		return add(lp, true);
	}

        @Override
	public void backgroundOperation(JobId processId, BackgroundJob lp) {
		add(processId, lp, true);
	}

        @Override
	public void addBackgroundListener(BackgroundListener listener) {
		listeners.add(listener);
	}

        @Override
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

        @Override
	public JobId nonBlockingBackgroundOperation(BackgroundJob lp) {
		return add(lp, false);
	}

        @Override
	public void nonBlockingBackgroundOperation(JobId processId, BackgroundJob lp) {
		add(processId, lp, false);
	}

        @Override
        public List<Job> getActiveJobs() {
                return Arrays.asList(getJobs());
        }

}
