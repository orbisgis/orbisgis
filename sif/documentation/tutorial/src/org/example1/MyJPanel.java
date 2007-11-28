package org.example1;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class MyJPanel extends JPanel {
	public MyJPanel() {
		final Component jComboBox = new JComboBox(new String[] { "aaa", "bbb",
				"ccc" });
		add(jComboBox);
	}
}
