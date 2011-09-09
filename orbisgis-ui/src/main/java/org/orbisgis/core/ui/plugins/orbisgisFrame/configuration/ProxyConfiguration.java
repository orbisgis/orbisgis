package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.orbisgis.core.Services;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.multiInputPanel.InputType;
import org.orbisgis.core.sif.multiInputPanel.IntType;
import org.orbisgis.core.sif.multiInputPanel.PasswordType;
import org.orbisgis.core.sif.multiInputPanel.StringType;
import org.orbisgis.utils.I18N;

public class ProxyConfiguration implements IConfiguration {
	private static final String SYSTEM_SOCKS_PROXY_PORT = "socksProxyPort";
	private static final String SYSTEM_SOCKS_PROXY_HOST = "socksProxyHost";
	private static final String SYSTEM_FTP_PROXY_PORT = "ftp.proxyPort";
	private static final String SYSTEM_FTP_PROXY_HOST = "ftp.proxyHost";
	private static final String SYSTEM_HTTP_PROXY_PORT = "http.proxyPort";
	private static final String SYSTEM_HTTP_PROXY_HOST = "http.proxyHost";

	private static final String PROXY_HOST_PROPERTY = "org.orbisgis.core.ui.configuration.proxyHost";
	private static final String PROXY_PORT_PROPERTY = "org.orbisgis.core.ui.configuration.proxyPort";
	private static final String PROXY_USER_PROPERTY = "org.orbisgis.core.ui.configuration.proxyUser";
	private static final String PROXY_PASSWORD_PROPERTY = "org.orbisgis.core.ui.configuration.proxyPassword";

	private JPanel panel;
	private StringType host, user;
	private PasswordType pass;
	private IntType port;
	private JCheckBox proxyCheck, authCheck;
	private String userValue, passValue;

	@Override
	public JComponent getComponent() {
		if (panel == null) {
			CRFlowLayout layout = new CRFlowLayout();
			layout.setVgap(40);
			panel = new JPanel(layout);
			host = new StringType(25);
			port = new IntType(6);
			user = new StringType(25);
			pass = new PasswordType(25);

			proxyCheck = new JCheckBox();
			proxyCheck.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!proxyCheck.isSelected()) {
						authCheck.setSelected(false);
						host.setEditable(false);
						port.setEditable(false);
						user.setEditable(false);
						pass.setEditable(false);
					} else {
						host.setEditable(true);
						port.setEditable(true);
					}
				}
			});

			authCheck = new JCheckBox();
			authCheck.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (authCheck.isSelected()) {
						proxyCheck.setSelected(true);
						host.setEditable(true);
						port.setEditable(true);
						user.setEditable(true);
						pass.setEditable(true);
					} else {
						user.setEditable(false);
						pass.setEditable(false);
					}
				}
			});

			String[] proxyLabels = {
					I18N.getString("orbisgis.org.orbisgis.core.hostName"),
					I18N.getString("orbisgis.org.orbisgis.core.port") };
			InputType[] proxyInputs = { host, port };
			JPanel proxyPanel = new ConfigUnitPanel(I18N
					.getString("orbisgis.org.orbisgis.core.proxy"), proxyCheck,
					I18N.getString("orbisgis.org.orbisgis.core.enableProxy"), proxyLabels, proxyInputs);

			String[] authLabels = {
					I18N.getString("orbisgis.org.orbisgis.core.userName"),
					I18N.getString("orbisgis.org.orbisgis.core.password") };
			InputType[] authInputs = { user, pass };
			JPanel authPanel = new ConfigUnitPanel(I18N
					.getString("orbisgis.org.orbisgis.core.authentication"),
					authCheck, I18N.getString("orbisgis.org.orbisgis.core.enableAuthentification"), authLabels, authInputs);

			panel.add(proxyPanel);
			panel.add(new CarriageReturn());
			panel.add(authPanel);
		}

		Properties systemSettings = System.getProperties();
		String hostValue = systemSettings.getProperty(SYSTEM_HTTP_PROXY_HOST);
		String portValue = systemSettings.getProperty(SYSTEM_HTTP_PROXY_PORT);

		host.setValue(hostValue);
		port.setValue(portValue);
		user.setValue(userValue);
		pass.setValue(passValue);

		boolean enableProxy, enableAuth;
		if (hostValue != null && portValue != null) {
			enableProxy = true;
			if (userValue != null && passValue != null) {
				enableAuth = true;
			} else {
				enableAuth = false;
			}
		} else {
			enableProxy = false;
			enableAuth = false;
		}

		proxyCheck.setSelected(enableProxy);
		host.setEditable(enableProxy);
		port.setEditable(enableProxy);
		authCheck.setSelected(enableAuth);
		user.setEditable(enableAuth);
		pass.setEditable(enableAuth);

		return panel;
	}

	@Override
	public void loadAndApply() {
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		String hostValue = bc.getProperty(PROXY_HOST_PROPERTY);
		String portValue = bc.getProperty(PROXY_PORT_PROPERTY);
		userValue = bc.getProperty(PROXY_USER_PROPERTY);
		passValue = bc.getProperty(PROXY_PASSWORD_PROPERTY);

		apply(hostValue, portValue);
	}

	@Override
	public void applyUserInput() {
		String hostValue = null;
		String portValue = null;
		userValue = null;
		passValue = null;

		if (proxyCheck.isSelected()) {
			hostValue = host.getValue();
			portValue = port.getValue();
			if (authCheck.isSelected()) {
				userValue = user.getValue();
				passValue = pass.getValue();
			}

		}

		apply(hostValue, portValue);
	}

	private void apply(String hostValue, String portValue) {
		Properties systemSettings = System.getProperties();
		Authenticator auth = null;
		if (hostValue != null && portValue != null) {
			systemSettings.put(SYSTEM_HTTP_PROXY_HOST, hostValue);
			systemSettings.put(SYSTEM_HTTP_PROXY_PORT, portValue);
			systemSettings.put(SYSTEM_FTP_PROXY_HOST, hostValue);
			systemSettings.put(SYSTEM_FTP_PROXY_PORT, portValue);
			systemSettings.put(SYSTEM_SOCKS_PROXY_HOST, hostValue);
			systemSettings.put(SYSTEM_SOCKS_PROXY_PORT, portValue);
			System.setProperties(systemSettings);
			if (userValue != null && passValue != null) {
				auth = new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(userValue, passValue
								.toCharArray());
					}
				};
			}
		} else {
			systemSettings.remove(SYSTEM_HTTP_PROXY_HOST);
			systemSettings.remove(SYSTEM_HTTP_PROXY_PORT);
			systemSettings.remove(SYSTEM_FTP_PROXY_HOST);
			systemSettings.remove(SYSTEM_FTP_PROXY_PORT);
			systemSettings.remove(SYSTEM_SOCKS_PROXY_HOST);
			systemSettings.remove(SYSTEM_SOCKS_PROXY_PORT);
		}

		System.setProperties(systemSettings);
		Authenticator.setDefault(auth);
	}

	@Override
	public void saveApplied() {
		Properties systemSettings = System.getProperties();
		String hostValue = systemSettings.getProperty(SYSTEM_HTTP_PROXY_HOST);
		String portValue = systemSettings.getProperty(SYSTEM_HTTP_PROXY_PORT);

		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		if (hostValue != null && portValue != null) {
			bc.setProperty(PROXY_HOST_PROPERTY, hostValue);
			bc.setProperty(PROXY_PORT_PROPERTY, portValue);
			if (userValue != null && passValue != null) {
				bc.setProperty(PROXY_USER_PROPERTY, userValue);
				bc.setProperty(PROXY_PASSWORD_PROPERTY, passValue);
			} else {
				bc.removeProperty("proxyUser");
				bc.removeProperty("proxyPassword");
			}
		} else {
			bc.removeProperty(PROXY_HOST_PROPERTY);
			bc.removeProperty(PROXY_PORT_PROPERTY);
			bc.removeProperty(PROXY_USER_PROPERTY);
			bc.removeProperty(PROXY_PASSWORD_PROPERTY);
		}
	}

	@Override
	public String validateInput() {
		if (proxyCheck.isSelected()) {
			if (host.getValue().equals("")) {
				return I18N
						.getString("orbisgis.org.orbisgis.core.hostCannotBeNull");
			}

			try {
				int p = Integer.parseInt(port.getValue());
				if (p < 0 || p > 65535) {
					return I18N
							.getString("orbisgis.org.orbisgis.core.portNumber");
				}
			} catch (NumberFormatException e) {
				return I18N.getString("orbisgis.org.orbisgis.core.portNumber");
			}

			if (authCheck.isSelected()) {
				if (user.getValue().equals("")) {
					return I18N
							.getString("orbisgis.org.orbisgis.core.userNameCannotBeNull");
				}

				if (pass.getValue().equals("")) {
					return I18N
							.getString("orbisgis.org.orbisgis.core.passwordCannotBeNull");
				}
			}
		}
		return null;
	}
}
