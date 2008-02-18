/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
package org.orbisgis;


import java.util.Iterator;
import java.util.Stack;

public class ProgressMonitor implements IProgressMonitor {

	private float progress = 0;

	private Stack<Task> tasks = new Stack<Task>();

	private boolean cancelled;

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

	public synchronized boolean isCancelled() {
		return cancelled;
	}

	public synchronized void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
