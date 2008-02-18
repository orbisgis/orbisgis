package org.orbisgis.pluginManager.background;

import java.util.ArrayList;

import org.orbisgis.IProgressMonitor;
import org.orbisgis.ProgressMonitor;

public class Job implements LongProcess, IProgressMonitor {

	private ProcessId processId;
	private LongProcess lp;
	private ProgressMonitor pm;
	private JobQueue jobQueue;
	private ArrayList<ProgressListener> listeners = new ArrayList<ProgressListener>();

	public Job(ProcessId processId, LongProcess lp, JobQueue jobQueue) {
		this.processId = processId;
		this.lp = lp;
		this.pm = new ProgressMonitor(lp.getTaskName());
		this.jobQueue = jobQueue;
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

	public ProcessId getId() {
		return processId;
	}

	public void setProcess(LongProcess lp) {
		this.lp = lp;
	}

	public void cancel() {
		pm.setCancelled(true);
	}

	public void start() {
		RunnableLongProcess runnable = new RunnableLongProcess(jobQueue, this,
				this);
		Thread currentThread = new Thread(runnable);
		currentThread.start();
		jobQueue.processChanged();
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

	public int getProgress() {
		return pm.getProgress();
	}

	public void init(String taskName) {
		pm.init(taskName);
		jobQueue.processChanged();
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

	public void startTask(String taskName, int percentage) {
		pm.startTask(taskName, percentage);
		fireSubTaskStarted();
	}

	public boolean isBlocking() {
		return lp instanceof LongBlockingProcess;
	}

}
