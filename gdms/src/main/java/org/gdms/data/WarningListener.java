package org.gdms.data;

/**
 * This interface manages exceptions of process that will not abort its
 * operation on such exceptions
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface WarningListener {

	/**
	 * The process throwed a warning
	 *
	 * @param msg
	 * @param t
	 * @param source
	 */
	public void throwWarning(String msg, Throwable t, Object source);

	/**
	 * The process throwed a warning
	 * @param msg
	 */
	public void throwWarning(String msg);

}
