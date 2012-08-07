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
package org.orbisgis.view.background;

import java.util.ArrayList;
import org.orbisgis.progress.DefaultProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;

public class Job implements BackgroundJob, ProgressMonitor {

	private JobId processId;
	private BackgroundJob lp;
	private ProgressMonitor pm;
	private BackgroundManager jobQueue;
	private ArrayList<ProgressListener> listeners = new ArrayList<ProgressListener>();
	private Thread currentThread = null;
	private boolean isBlocking;

	public Job(JobId processId, BackgroundJob lp, BackgroundManager jobQueue,
			boolean isBlocking) {
		this.processId = processId;
		this.lp = lp;
		this.pm = new DefaultProgressMonitor(lp.getTaskName(), 100);
		this.jobQueue = jobQueue;
		this.isBlocking = isBlocking;
	}

	public synchronized void addProgressListener(ProgressListener listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeProgressListener(ProgressListener listener) {
		this.listeners.remove(listener);
	}

        @Override
	public String getTaskName() {
		return lp.getTaskName();
	}

        @Override
	public void run(ProgressMonitor pm) {
		lp.run(pm);
	}

	public JobId getId() {
		return processId;
	}

	public void setProcess(BackgroundJob lp) {
		this.lp = lp;
	}

	public void cancel() {
		pm.setCancelled(true);
	}

        /**
         * Gets an instance of {@code Runnable} that is ready to be run.
         * @return
         */
        public Thread getReadyRunnable(){
                currentThread = new Thread(new RunnableBackgroundJob(jobQueue, this, this));
                return currentThread;
        }

	public synchronized void start() {
		RunnableBackgroundJob runnable = new RunnableBackgroundJob(jobQueue,
				this, this);
		currentThread = new Thread(runnable);
		currentThread.start();
	}

	public void endTask() {
		pm.endTask();
		fireSubTaskFinished();
	}

	private synchronized void fireSubTaskFinished() {
		for (ProgressListener listener : listeners) {
			listener.subTaskFinished(this);
		}
	}

	private synchronized void fireSubTaskStarted() {
		for (ProgressListener listener : listeners) {
			listener.subTaskStarted(this);
		}
	}

	public void init(String taskName, long end) {
		pm.init(taskName, end);
		fireProgressTo();
	}

	public boolean isCancelled() {
		return pm.isCancelled();
	}

	public void progressTo(long progress) {
		pm.progressTo(progress);
		fireProgressTo();
	}

	private synchronized void fireProgressTo() {
		for (ProgressListener listener : listeners) {
			listener.progressChanged(this);
		}
	}

	public void setCancelled(boolean cancelled) {
		pm.setCancelled(cancelled);
	}

	public void startTask(String taskName, long end) {
		pm.startTask(taskName, end);
		fireSubTaskStarted();
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public String getCurrentTaskName() {
		return pm.getCurrentTaskName();
	}

	public int getOverallProgress() {
		return pm.getOverallProgress();
	}

	public int getCurrentProgress() {
		return pm.getCurrentProgress();
	}

	public synchronized boolean isStarted() {
		return currentThread != null;
	}

	public void clear() {
		lp = new BackgroundJob() {

                        @Override
			public void run(ProgressMonitor pm) {
			}

                        @Override
			public String getTaskName() {
				return "iddle";
			}

		};
	}

}
