package org.gdms.driver;

/**
 * Interface to be implement by the File drivers that as also RW capabilities
 * 
 */

public interface FileReadWriteDriver extends FileDriver {
	/**
	 * Sort of File::canWrite() method
	 * 
	 * @return
	 */
	public boolean isEditable();
}