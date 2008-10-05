package org.orbisgis.configurations;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.configuration.IConfiguration;
import org.orbisgis.configurations.ui.ConfigUnitPanel;
import org.sif.multiInputPanel.InputType;
import org.sif.multiInputPanel.StringType;

public class UpdateURLConfiguration implements IConfiguration {
	private JPanel panel;
	private StringType url;
	private JCheckBox checkbox;

	@Override
	public JComponent getComponent() {
		if (panel == null) {
			panel = new JPanel(new GridBagLayout());

			url = new StringType(30);
			checkbox = new JCheckBox();
			checkbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					url.setEditable(checkbox.isSelected());
				}
			});
			String[] labels = { "URL: " };
			InputType[] inputs = { url };
			JPanel urlPanel = new ConfigUnitPanel("Automatic Updates",
					checkbox, "Enable automatic updates", labels, inputs);
			urlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(), "Automatic Updates"));

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(30, 10, 10, 10);
			panel.add(urlPanel, c);
			c.gridy = 1;
			c.insets = new Insets(10, 10, 10, 10);
			c.weighty = 0.1;
			panel.add(new JLabel(""), c);
		}

		return panel;
	}

	@Override
	public void loadAndApply() {
		// TODO
	}

	@Override
	public String validateInput() {
		if (panel != null && checkbox.isSelected() && url.getValue().equals("")) {
			return "You must specify a correct URL";
		}

		return null;
	}

	@Override
	public void applyUserInput() {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveApplied() {
		// TODO Auto-generated method stub
	}
}
