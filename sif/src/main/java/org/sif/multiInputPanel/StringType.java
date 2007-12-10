package org.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JTextField;

import org.sif.SQLUIPanel;

public class StringType implements InputType {

	private JTextField comp = new JTextField();

	public StringType(int columns) {
		comp.setColumns(columns);
	}

	public StringType() {
		comp.setColumns(5);
	}

	public Component getComponent() {
		return comp;
	}

	public int getType() {
		return SQLUIPanel.STRING;
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
