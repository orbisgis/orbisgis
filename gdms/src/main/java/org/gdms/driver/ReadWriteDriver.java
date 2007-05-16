package org.gdms.driver;

public interface ReadWriteDriver extends ReadOnlyDriver {
	public boolean isEditable();
}