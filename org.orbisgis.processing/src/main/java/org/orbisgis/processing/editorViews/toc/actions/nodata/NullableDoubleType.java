package org.orbisgis.processing.editorViews.toc.actions.nodata;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.sif.SQLUIPanel;
import org.sif.multiInputPanel.InputType;

public class NullableDoubleType implements InputType, ChangeListener {

	private JPanel panel = new JPanel();
	private JCheckBox jCheckBox = new JCheckBox();
	private JTextField jTextField = new JTextField();

	public NullableDoubleType(int columns) {
		jCheckBox.setSelected(false);
		jCheckBox.addChangeListener(this);
		jTextField.setColumns(columns);
		jTextField.setEnabled(false);
		panel.add(jCheckBox);
		panel.add(jTextField);
	}

	public Component getComponent() {
		return panel;
	}

	public int getType() {
		return SQLUIPanel.DOUBLE;
	}

	public String getValue() {
		if (jCheckBox.isSelected()) {
			return jTextField.getText();
		}
		return null;
	}

	public boolean isPersistent() {
		return true;
	}

	public void setValue(String value) {
		jCheckBox.setSelected(value != null);
		jTextField.setText(value);

	}

	public void stateChanged(ChangeEvent e) {
		jTextField.setEnabled(jCheckBox.isSelected());
	}

}
