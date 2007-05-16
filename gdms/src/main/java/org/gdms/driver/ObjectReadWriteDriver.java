package org.gdms.driver;

/**
 * Interface to be implement by the Object driver that as also RW capabilities
 * 
 */
public interface ObjectReadWriteDriver extends ObjectDriver {
	public boolean isEditable();
}