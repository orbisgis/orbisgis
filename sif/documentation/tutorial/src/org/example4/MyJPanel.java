package org.example4;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class MyJPanel extends JPanel {
	private JComboBox jComboBox;

	public MyJPanel() {
		jComboBox = new JComboBox(new String[] { null, "aaa", "bbb", "ccc" });
		add(jComboBox);
	}

	public String getSelection() {
		return (String) jComboBox.getSelectedItem();
	}
}
