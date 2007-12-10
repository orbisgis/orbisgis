package org.sif.multiInputPanel;

import java.awt.Component;

public interface InputType {

	int getType();

	Component getComponent();

	String getValue();

	void setValue(String value);
	
	boolean isPersistent();

}
