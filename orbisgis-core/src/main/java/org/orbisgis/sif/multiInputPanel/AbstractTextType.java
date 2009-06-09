package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JTextField;

public abstract class AbstractTextType implements InputType {
	private JTextField comp = new JTextField();

	public AbstractTextType(int columns) {
		comp.setColumns(columns);
	}

	public AbstractTextType() {
		comp.setColumns(5);
	}

	public Component getComponent() {
		return comp;
	}

	public String getValue() {
		return comp.getText();
	}

	public void setValue(String value) {
		comp.setText(value);
	}

	public boolean isPersistent() {
		return true;
	}

	public void setEditable(boolean b) {
		comp.setEditable(b);
	}

}
