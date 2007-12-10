package org.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JPasswordField;

import org.sif.SQLUIPanel;

public class PasswordType implements InputType {

	private JPasswordField comp = new JPasswordField();

	public PasswordType(int columns) {
		comp.setColumns(columns);
	}

	public PasswordType() {
	}

	public Component getComponent() {
		return comp;
	}

	public int getType() {
		return SQLUIPanel.STRING;
	}

	public String getValue() {
		return new String(comp.getPassword());
	}

	public void setValue(String value) {
		comp.setText(value);
	}

	public boolean isPersistent() {
		return false;
	}

}
