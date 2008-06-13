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

import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;

public class Job implements BackgroundJob, IProgressMonitor {

	private JobId processId;
	private BackgroundJob lp;
	private ProgressMonitor pm;
	private JobQueue jobQueue;
	private ArrayList<ProgressListener> listeners = new ArrayList<ProgressListener>();
	private Thread currentThread = null;
	private boolean isBlocking;

	public Job(JobId processId, BackgroundJob lp, JobQueue jobQueue,
			boolean isBlocking) {
		this.processId = processId;
		this.lp = lp;
		this.pm = new ProgressMonitor(lp.getTaskName());
		this.jobQueue = jobQueue;
		this.isBlocking = isBlocking;
	}

	public synchronized void addProgressListener(ProgressListener listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeProgressListener(ProgressListener listener) {
		this.listeners.remove(listener);
	}

	public String getTaskName() {
		return lp.getTaskName();
	}

	public void run(IProgressMonitor pm) {
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

	public void init(String taskName) {
		pm.init(taskName);
		fireProgressTo();
	}

	public boolean isCancelled() {
		return pm.isCancelled();
	}

	public void progressTo(int progress) {
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

	public void startTask(String taskName) {
		pm.startTask(taskName);
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

			public void run(IProgressMonitor pm) {
			}

			public String getTaskName() {
				return "iddle";
			}

		};
	}

}
