package org.gdms.data;

import java.io.File;

/**
 * Indicates the commit was successful but a problem happened freeing
 * resources. Now the InternalDataSource is in an unpredictable status
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class FreeingResourcesException extends Exception {

	private File tempFile;

	public FreeingResourcesException(String message, Throwable cause, File temp) {
		super(message, cause);
		this.tempFile = temp;
	}

	public FreeingResourcesException(Throwable cause) {
		super(cause);
	}

	/**
	 * If the exception is thrown when commiting changes to a file, the contents
	 * can be in another temporal file. If so, this method returns the path to
	 * that file
	 *
	 * @return
	 */
	public File getTempFile() {
		return tempFile;
	}

}
