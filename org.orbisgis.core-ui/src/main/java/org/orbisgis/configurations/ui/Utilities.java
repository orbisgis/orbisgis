package org.orbisgis.configurations.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Utilities {
	public static JPanel createPanel(String title, JCheckBox checkbox,
			String enableText, final AbstractInput... input) {
		JPanel proxyPanel = new JPanel(new GridBagLayout());
		proxyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), title));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 10);
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = 0;

		if (checkbox != null) {
			JPanel checkPanel = new JPanel(new FlowLayout());
			checkbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox box = (JCheckBox) e.getSource();
					for (AbstractInput abstractInput : input) {
						abstractInput.setEditable(box.isSelected());
					}
				}
			});

			checkPanel.add(checkbox);
			checkPanel.add(new JLabel(enableText));
			proxyPanel.add(checkPanel, c);
			c.gridy++;
		}

		for (int i = 0; i < input.length - 1; i++) {
			proxyPanel.add(input[i], c);
			c.gridy++;
		}

		c.insets = new Insets(0, 10, 10, 10);
		proxyPanel.add(input[input.length - 1], c);

		return proxyPanel;
	}
}
