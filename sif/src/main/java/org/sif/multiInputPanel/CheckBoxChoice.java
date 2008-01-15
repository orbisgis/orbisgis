package org.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JCheckBox;

import org.sif.SQLUIPanel;

public class CheckBoxChoice implements InputType {

	private  JCheckBox jCheckBox;
	
	
	public CheckBoxChoice(boolean b) {
		jCheckBox = new JCheckBox();
		jCheckBox.setSelected(b);
		
		
	}

	public Component getComponent() {
		
		return jCheckBox;
	}

	public int getType() {
		
		return SQLUIPanel.STRING;
	}

	public String getValue() {
		return Boolean.toString(jCheckBox.isSelected());
	}

	public boolean isPersistent() {
		
		return true;
	}

	public void setValue(String value) {
		jCheckBox.setSelected(Boolean.parseBoolean(value));

	}

}
