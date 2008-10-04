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

	@Override
	public JComponent getComponent() {
		// Creates the panel lazily
		if (panel == null) {
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

		return panel;
	}

	@Override
	public void loadAndApply() {
		// TODO
	}

	@Override
	public String validateInput() {
		if (url != null && url.getValue().equals("")) {
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
