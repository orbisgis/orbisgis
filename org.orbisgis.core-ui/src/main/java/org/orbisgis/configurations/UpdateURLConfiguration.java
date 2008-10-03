package org.orbisgis.configurations;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.configuration.IConfiguration;
import org.orbisgis.configurations.ui.TextInput;
import org.orbisgis.configurations.ui.Utilities;

public class UpdateURLConfiguration implements IConfiguration {

	private JPanel panel;
	private TextInput url;

	/**
	 * Creates a new configuration for the automatic updates URL
	 */
	public UpdateURLConfiguration() {
		panel = new JPanel(new GridBagLayout());

		url = new TextInput("URL: ", true, 250);
		JPanel urlPanel = Utilities.createPanel("Automatic Updates", null,
				null, url);
		urlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Automatic Updates"));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 5;
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(30, 10, 10, 10);
		panel.add(urlPanel, c);

		c.gridy = 1;
		c.insets = new Insets(10, 10, 10, 10);
		c.weighty = 0.1;
		panel.add(new JLabel(""), c);
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public void load() {
		// TODO load
	}

	@Override
	public void save() {
		// TODO save
	}

	@Override
	public String validateInput() {
		if (url.getValue().equals("")) {
			return "You must specify a correct URL";
		}

		return null;
	}
}
