/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.sql.internalExceptions;

/**
 * Reseteable timer. After a cancelTimer or after the execution of the scheduled
 * task the object becomes unuseful: It's not possible to schedule more tasks
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class Timer {
	private boolean cancel = false;

	private Thread timerThread;

	/**
	 * schedules a task
	 * 
	 * @param task
	 *            task to schedule
	 * @param delay
	 *            delay in milliseconds
	 */
	public void schedule(Task task, long delay) {
		timerThread = new Thread(new TimerThread(delay, task));
		timerThread.start();
	}

	/**
	 * restarts the timer
	 */
	public void resetTimer() {
		cancel = false;
		timerThread.interrupt();
	}

	/**
	 * cancels the timer. It causes the timer to be unuseful. After this call
	 * you cannot schedule more tasks
	 */
	public void cancelTimer() {
		cancel = true;
		timerThread.interrupt();
	}

	/**
	 * timer thread
	 * 
	 * @author Fernando Gonz�lez Cort�s
	 */
	public class TimerThread implements Runnable {
		long timeout;

		Task task;

		/**
		 * Creates a new TimerThread.
		 * 
		 * @param timeout
		 *            delay in milliseconds
		 * @param task
		 *            task to schedule
		 */
		public TimerThread(long timeout, Task task) {
			this.timeout = timeout;
			this.task = task;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public synchronized void run() {
			while (true) {
				try {
					// Waits the delay
					wait(timeout);

					// executes the task
					task.execute();

					// exits
					break;
				} catch (InterruptedException e) {
					if (cancel) {
						break;
					} else {
						// Volvemos a esperar
					}
				}
			}
		}
	}
}
