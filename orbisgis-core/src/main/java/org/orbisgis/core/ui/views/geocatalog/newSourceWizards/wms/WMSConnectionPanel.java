package org.orbisgis.core.ui.views.geocatalog.newSourceWizards.wms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.gvsig.remoteClient.wms.WMSClient;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.WMSClientPool;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.SQLUIPanel;

public class WMSConnectionPanel extends JPanel implements SQLUIPanel {

	private static final String TITLE_PREFIX = "Name:";
	private static final String VERSION_PREFIX = "Version:";
	private JTextField txtURL;
	private JLabel lblVersion;
	private JLabel lblTitle;
	private JTextArea txtDescription;

	private WMSClient client;

	public WMSConnectionPanel() {
		GridBagLayout gl = new GridBagLayout();
		this.setLayout(gl);
		GridBagConstraints c = new GridBagConstraints();

		// Connection panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		JPanel pnlURL = new JPanel();
		pnlURL.setBorder(BorderFactory.createTitledBorder("Server URL"));
		txtURL = new JTextField(40);
		JButton btnConnect = new JButton("Connect...");
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		pnlURL.add(txtURL);
		pnlURL.add(btnConnect);
		this.add(pnlURL, c);

		// Info panel
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		JPanel pnlInfo = new JPanel();
		pnlInfo.setLayout(new BorderLayout());
		pnlInfo.setBorder(BorderFactory
				.createTitledBorder("Server information"));
		JPanel pnlNorth = new JPanel();
		pnlNorth.setLayout(new CRFlowLayout());
		lblVersion = new JLabel(VERSION_PREFIX);
		lblTitle = new JLabel(TITLE_PREFIX);
		pnlNorth.add(lblVersion);
		pnlNorth.add(new CarriageReturn());
		pnlNorth.add(lblTitle);
		pnlInfo.add(pnlNorth, BorderLayout.NORTH);
		txtDescription = new JTextArea("\n\n\n\n\n\n");
		txtDescription.setEditable(false);
		JScrollPane comp = new JScrollPane(txtDescription);
		pnlInfo.add(comp, BorderLayout.CENTER);
		this.add(pnlInfo, c);
	}

	private void connect() {
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
				String wmsURL = txtURL.getText().trim();
				try {
					client = WMSClientPool.getWMSClient(wmsURL);
					client.getCapabilities(null, false, null);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							lblVersion.setText(VERSION_PREFIX
									+ changeNullForEmptyString(client
											.getVersion()));
							lblTitle.setText(TITLE_PREFIX
									+ changeNullForEmptyString(client
											.getServiceName()));
							txtDescription.setText(client
									.getServiceInformation().abstr);
							txtDescription.setCaretPosition(0);
						}
					});
				} catch (ConnectException e) {
					Services.getErrorManager().error(
							"Cannot connect to specified url: " + wmsURL, e);
				} catch (IOException e) {
					Services.getErrorManager().error(
							"Cannot get capabilities: " + wmsURL, e);
				}
			}

			@Override
			public String getTaskName() {
				return "Connecting server";
			}
		});
	}

	private String changeNullForEmptyString(String property) {
		if (property == null) {
			return "";
		} else {
			return property;
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getTitle() {
		return "WMS server connection";
	}

	@Override
	public String validateInput() {
		if (client == null) {
			return "Connect to a WMS server";
		}

		return null;
	}

	@Override
	public String[] getErrorMessages() {
		return null;
	}

	@Override
	public String[] getFieldNames() {
		return new String[] { "server-url" };
	}

	@Override
	public int[] getFieldTypes() {
		return new int[] { STRING };
	}

	@Override
	public String getId() {
		return "org.orbisgis.newResourceWizards.WMSConnection";
	}

	@Override
	public String[] getValidationExpressions() {
		return null;
	}

	@Override
	public String[] getValues() {
		return new String[] { txtURL.getText() };
	}

	@Override
	public void setValue(String fieldName, String fieldValue) {
		txtURL.setText(fieldValue);
	}

	@Override
	public boolean showFavorites() {
		return true;
	}

	@Override
	public URL getIconURL() {
		return null;
	}

	@Override
	public String getInfoText() {
		return "Input a server url to connect to";
	}

	@Override
	public String initialize() {
		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

	public WMSClient getWMSClient() {
		return client;
	}

}
