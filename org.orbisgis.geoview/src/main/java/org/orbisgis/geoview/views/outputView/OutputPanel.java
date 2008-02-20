package org.orbisgis.geoview.views.outputView;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class OutputPanel extends JPanel {

	private JTextArea jTextArea;

	public OutputPanel() {
		this.setLayout(new BorderLayout());
		jTextArea = new JTextArea();
		this.add(new JScrollPane(jTextArea));

	}

	public void add(String out) {
		if (jTextArea.getText().length() == 0) {
			jTextArea.setText(out);
		} else {
			jTextArea.append(out);
		}
		jTextArea.setCaretPosition(jTextArea.getText().length());
	}

}
