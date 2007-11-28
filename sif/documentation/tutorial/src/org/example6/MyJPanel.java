package org.example6;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MyJPanel extends JPanel {
	private JComboBox jComboBox;
	private JTextField jTextField;

	public MyJPanel() {
		jComboBox = new JComboBox(new String[] { null, "aaa", "bbb", "ccc" });
		jTextField = new JTextField("enter a double here");
		add(jComboBox);
		add(jTextField);
	}

	public void setJComboBox(String fieldValue) {
		jComboBox.setSelectedItem(fieldValue);
	}

	public void setJTextField(String textField) {
		jTextField.setText(textField);
	}

	public String getJComboBoxSelection() {
		return (String) jComboBox.getSelectedItem();
	}

	public String getJTextFieldEntry() {
		return jTextField.getText();
	}
}
