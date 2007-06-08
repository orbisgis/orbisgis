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
