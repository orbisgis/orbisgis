package org.orbisgis.configurations;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.configuration.IConfiguration;
import org.orbisgis.configurations.ui.PasswordInput;
import org.orbisgis.configurations.ui.TextInput;
import org.orbisgis.configurations.ui.Utilities;

public class ProxyConfiguration implements IConfiguration {

	private JPanel panel;
	private TextInput host, port, user;
	private PasswordInput pass;
	private JCheckBox proxyCheck, authCheck;

	/**
	 * Creates a new configuration for the proxy
	 */
	public ProxyConfiguration() {
		panel = new JPanel(new GridBagLayout());
		host = new TextInput("Host: ", false, 250);
		port = new TextInput("Port: ", false, 75);
		user = new TextInput("User: ", false, 250);
		pass = new PasswordInput("Pass: ", false, 250);
		proxyCheck = new JCheckBox();
		authCheck = new JCheckBox();

		JPanel proxyPanel = Utilities.createPanel("Proxy", proxyCheck,
				"Enable Proxy", host, port);
		JPanel authPanel = Utilities.createPanel("Authentication", authCheck,
				"Enable Authentication", user, pass);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 5;
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(30, 10, 10, 10);
		panel.add(proxyPanel, c);

		c.gridy = 1;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(authPanel, c);

		c.weighty = 0.1;
		c.gridy = 2;
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
		Properties systemSettings = System.getProperties();
		systemSettings.put("http.proxyHost", "10.0.0.3");
		systemSettings.put("http.proxyPort", "3128");
		systemSettings.put("socksProxyHost", "10.0.0.3");
		systemSettings.put("socksProxyPort", "3128");
		System.setProperties(systemSettings);

		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				String proxyUser = "dos";
				String proxyPassword = "dos";
				return new PasswordAuthentication(proxyUser, proxyPassword
						.toCharArray());
			}
		});

		try {
			URL url = new URL("http://www.google.com/");
			URLConnection con = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(con
					.getInputStream()));
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String validateInput() {
		if (proxyCheck.isSelected()) {
			if (host.getValue().equals("")) {
				return "You must specify a correct host";
			}

			try {
				int p = Integer.parseInt(port.getValue());
				if (p < 0 || p > 65535) {
					return "You must specify a correct port";
				}
			} catch (NumberFormatException e) {
				return "You must specify a correct port";
			}
		}

		if (authCheck.isSelected()) {
			if (user.getValue().equals("")) {
				return "You must specify a correct user";
			}

			if (pass.getValue().equals("")) {
				return "You must specify a correct password";
			}
		}

		return null;
	}
}
