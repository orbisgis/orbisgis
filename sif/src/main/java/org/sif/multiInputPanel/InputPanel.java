package org.sif.multiInputPanel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InputPanel extends JPanel {

	public InputPanel(ArrayList<Input> inputs) {
		JPanel margin = new JPanel();
		GridLayout gl = new GridLayout(0, 2);
		margin.setLayout(gl);
		FlowLayout lfl = new FlowLayout();
		lfl.setAlignment(FlowLayout.LEFT);
		FlowLayout rfl = new FlowLayout();
		rfl.setAlignment(FlowLayout.RIGHT);
		String currentGroup = null;
		JPanel currentPanel = null;
		for (Input input : inputs) {
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(rfl);
			JLabel label = new JLabel(input.getText());
			labelPanel.add(label);
			JPanel compPanel = new JPanel();
			compPanel.setLayout(lfl);
			Component comp = input.getType().getComponent();
			if (comp != null) {
				compPanel.add(comp);
			}
			input.getType().setValue(input.getInitialValue());

			String group = input.getGroup();
			if ((group == null) || !group.equals(currentGroup)) {
				gl.setColumns(1);
				if (currentPanel != null) {
					margin.add(currentPanel);
				}
				currentGroup = group;
				currentPanel = new JPanel();
				currentPanel.setLayout(new GridLayout(0, 2));
				if (group != null) {
					currentPanel.setBorder(BorderFactory
							.createTitledBorder(currentGroup));
				}
			}
			currentPanel.add(labelPanel);
			currentPanel.add(compPanel);
		}
		if (currentPanel != null) {
			margin.add(currentPanel);
		}

		this.add(margin);
	}

}
