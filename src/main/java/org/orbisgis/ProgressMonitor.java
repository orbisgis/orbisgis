package org.orbisgis;


import java.util.Iterator;
import java.util.Stack;

public class ProgressMonitor implements IProgressMonitor {

	private float progress = 0;

	private Stack<Task> tasks = new Stack<Task>();

	public ProgressMonitor(String taskName) {
		init(taskName);
	}

	/**
	 * @param taskName
	 */
	public void init(String taskName) {
		progress = 0;
		tasks.push(new Task(taskName, 100, 0));
	}

	/**
	 * @param taskName
	 * @param percentage
	 */
	public void startTask(String taskName, int percentage) {
		tasks.push(new Task(taskName, percentage, (int) progress));
	}

	private class Task {

		String taskName;

		int percentage;

		int previousPercentage;

		private int basePercentage;

		public Task(String taskName, int percentage, int basePercentage) {
			this.taskName = taskName;
			this.percentage = percentage;
			this.basePercentage = basePercentage;
		}

	}

	/**
	 * 
	 */
	public void endTask() {
		Task t = tasks.pop();
		progress = t.basePercentage + getProgress(t.percentage);
	}

	private float getProgress(int progress) {
		Iterator<Task> it = tasks.iterator();
		float factor = 1;
		while (it.hasNext()) {
			Task task = it.next();
			factor *= factor * (task.percentage / 100.0);
		}

		return progress* factor;
	}

	/**
	 * @param progress
	 */
	public void progressTo(int progress) {
		this.progress = tasks.peek().basePercentage + getProgress(progress);
	}

	/**
	 * @return
	 */
	public int getProgress() {
		return (int) progress;
	}

	public String toString() {
		if (tasks.size() == 0) {
			return "finished: " + (int) progress;
		} else {
			return tasks.peek().taskName + ": " + (int) progress;
		}
	}

}
