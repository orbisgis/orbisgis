package org.gdms.sql.internalExceptions;

/**
 * timer executable task
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public interface Task {
	/**
	 * Method called when the Timer timeouts
	 */
	public void execute();
}
