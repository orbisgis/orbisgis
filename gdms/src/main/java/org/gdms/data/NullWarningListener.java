package org.gdms.data;

/**
 * "Deaf" warning listener
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class NullWarningListener implements WarningListener {

	public void throwWarning(String msg, Throwable t, Object source) {
	}

	public void throwWarning(String msg) {
	}
}
