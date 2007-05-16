package org.gdms.driver;

/**
 * Interface to be implement by the DB drivers that as also RW capabilities
 * 
 */
public interface DBReadWriteDriver extends DBDriver {
	/**
	 * Return true iff there is a unique field or a primary key in the DB table
	 * 
	 * @return
	 */
	public boolean isEditable();
}