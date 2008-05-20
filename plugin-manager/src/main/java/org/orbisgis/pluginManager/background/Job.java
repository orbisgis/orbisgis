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
