package org.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JTextField;

import org.sif.SQLUIPanel;

public class TextFieldType {

	private JTextField comp = new JTextField();
	private int type;

	public TextFieldType(int type, int columns) {
		comp.setColumns(columns);
		this.type = type;
	}

	public TextFieldType(int type) {
		this.type = type;
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
