package org.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JComboBox;

import org.sif.SQLUIPanel;

public class Choice implements InputType {

	private JComboBox comp;

	public Choice(String... choices) {
		comp = new JComboBox(choices);
	}

	public Component getComponent() {
		return comp;
	}

	public int getType() {
		return SQLUIPanel.STRING;
	}

	public String getValue() {
		return (String) comp.getSelectedItem();
	}

	public void setValue(String value) {
		comp.setSelectedItem(value);
	}

	public boolean isPersistent() {
		return true;
	}

}
