package org.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JTextField;

import org.sif.SQLUIPanel;

public class IntType implements InputType {

	private JTextField comp = new JTextField();

	public IntType(int columns) {
		comp.setColumns(columns);
	}

	public IntType() {
		comp.setColumns(5);
	}

	public Component getComponent() {
		return comp;
	}

	public int getType() {
		return SQLUIPanel.INT;
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

}
