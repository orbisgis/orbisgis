/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.gvsig.remoteClient.wms.WMSClient;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.WMSClientPool;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.SQLUIPanel;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.plugins.views.geocatalog.WMSGeocatalogPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class WMSConnectionPanel extends JPanel implements SQLUIPanel {

	private static final String TITLE_PREFIX = I18N
			.getString("orbisgis.org.orbisgis.name");
	private static final String VERSION_PREFIX = I18N
			.getString("orbisgis.org.orbisgis.version");
	private static final String WMSServerFile = "wmsServerList.txt";
	private JComboBox cmbURLServer;
	private JLabel lblVersion;
	private JLabel lblTitle;
	private JTextArea txtDescription;

	private WMSClient client;
	private ArrayList<String> serverswms;
        private LayerConfigurationPanel configPanel;

	public WMSConnectionPanel(LayerConfigurationPanel configPanel) {
		GridBagLayout gl = new GridBagLayout();
		this.setLayout(gl);
		GridBagConstraints c = new GridBagConstraints();
                this.configPanel = configPanel;

		// Connection panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		JPanel pnlURL = new JPanel(new BorderLayout());
		pnlURL.setBorder(BorderFactory.createTitledBorder(I18N
				.getString("orbisgis.org.orbisgis.wms.url")));

		serverswms = loadWMSServers();
		cmbURLServer = new JComboBox(serverswms.toArray(new String[serverswms
				.size()]));
		cmbURLServer.setEditable(true);
		cmbURLServer.setMaximumSize(new Dimension(100, 20));

		pnlURL.add(cmbURLServer, BorderLayout.NORTH);
		JToolBar wmsBtnManager = new JToolBar();
		wmsBtnManager.setFloatable(false);
		wmsBtnManager.setOpaque(false);

		JButton btnConnect = new JButton(IconLoader
				.getIcon("server_connect.png"));
		btnConnect.setToolTipText(I18N
				.getString("orbisgis.org.orbisgis.wms.serverConnect"));
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		btnConnect.setBorderPainted(false);

		JButton btnDelete = new JButton(IconLoader.getIcon("remove.png"));
		btnDelete.setToolTipText(I18N
				.getString("orbisgis.org.orbisgis.wms.url.delete"));
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String item = cmbURLServer.getSelectedItem().toString();
				if (serverswms.contains(item)) {
					serverswms.remove(item);
					saveWMSServerFile();
				}
                                cmbURLServer.removeItem(item);
			}
		});
		btnDelete.setBorderPainted(false);

		JButton btnUpdate = new JButton(IconLoader.getIcon("arrow_refresh.png"));
		btnUpdate.setToolTipText(I18N
				.getString("orbisgis.org.orbisgis.wms.url.reload"));
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<String> updateServersList = readWMSServerFile(WMSGeocatalogPlugIn.class
							.getResourceAsStream(WMSServerFile));

					for (String updatewms : updateServersList) {
						if (!serverswms.contains(updatewms)) {
							serverswms.add(updatewms);
							cmbURLServer.addItem(updatewms);
						}
					}
					saveWMSServerFile();
				} catch (IOException e1) {
					ErrorMessages.error(ErrorMessages.CannotUpdate + " "
							+ WMSServerFile, e1);
				}

			}
		});
		btnUpdate.setBorderPainted(false);

		wmsBtnManager.add(btnConnect);
		wmsBtnManager.add(btnDelete);
		wmsBtnManager.add(btnUpdate);
		pnlURL.add(wmsBtnManager, BorderLayout.SOUTH);
		this.add(pnlURL, c);

		// Info panel
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		JPanel pnlInfo = new JPanel();
		pnlInfo.setLayout(new BorderLayout());
		pnlInfo.setBorder(BorderFactory.createTitledBorder(I18N
				.getString("orbisgis.org.orbisgis.wms.serverInformation")));
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
		txtDescription.setLineWrap(true);
		JScrollPane comp = new JScrollPane(txtDescription);
		pnlInfo.add(comp, BorderLayout.CENTER);
		this.add(pnlInfo, c);
	}

	private ArrayList<String> loadWMSServers() {
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		File file = ws.getFile(WMSServerFile);
		try {
			if (file.exists()) {
				return readWMSServerFile(new FileInputStream(file));
			} else {
				return readWMSServerFile(WMSGeocatalogPlugIn.class
						.getResourceAsStream(WMSServerFile));
			}
		} catch (IOException e) {
			ErrorMessages.error(ErrorMessages.CannotFind + " " + WMSServerFile,
					e);
		}

		return null;
	}

	/**
	 * Read the wms servers file list to populate the combobox
	 * 
	 * @param layoutStream
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> readWMSServerFile(InputStream layoutStream)
			throws IOException {
		ArrayList<String> serversList = new ArrayList<String>();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(layoutStream));

		String str;
		while ((str = bufferedReader.readLine()) != null) {
			if (str.length() > 0) {
				if (!serversList.contains(str)) {
					serversList.add(str);
				}
			}
		}
		bufferedReader.close();
		layoutStream.close();

		return serversList;
	}

	public void saveWMSServerFile() {
		try {
			Workspace ws = (Workspace) Services.getService(Workspace.class);
			File file = ws.getFile(WMSServerFile);
			PrintWriter pw = new PrintWriter(file);
			for (String server : serverswms) {
				pw.println(server);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			ErrorMessages.error(ErrorMessages.CannotSave + " " + WMSServerFile,
					e);
		}
	}

	private void connect() {
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(ProgressMonitor pm) {
				String originalWmsURL = cmbURLServer.getSelectedItem().toString();
                                String wmsURL = originalWmsURL.trim();
                                try {
					if (client == null) {
						client = WMSClientPool.getWMSClient(wmsURL);
					}
					else {
						if(!client.getHost().equals(wmsURL)){
							client = WMSClientPool.getWMSClient(wmsURL);
						}
					}
                                        configPanel.setClient(client);
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

                                        if (!serverswms.contains(originalWmsURL)) {
						serverswms.add(originalWmsURL);
						saveWMSServerFile();
                                        }

				} catch (ConnectException e) {
					ErrorMessages.error(I18N
							.getString("orbisgis.errorMessages.wms.CannotConnect"
									+ " " + wmsURL), e);
				} catch (IOException e) {
					ErrorMessages
							.error(
									I18N
											.getString("orbisgis.errorMessages.wms.CannotGetCapabilities"
													+ " " + wmsURL), e);
				}
			}

			@Override
			public String getTaskName() {
				return I18N
						.getString("orbisgis.org.orbisgis.wms.connectingServer");
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
		return I18N.getString("orbisgis.org.orbisgis.wms.serverConnection");
	}

	@Override
	public String validateInput() {
		if (client == null) {
			return I18N.getString("orbisgis.org.orbisgis.wms.connectWMSServer");
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
		return "org.orbisgis.plugins.newResourceWizards.WMSConnection";
	}

	@Override
	public String[] getValidationExpressions() {
		return null;
	}

	@Override
	public String[] getValues() {
		Object[] items = cmbURLServer.getSelectedObjects();
		String[] values = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			values[i] = items[i].toString();
		}

		return values;
	}

	@Override
	public void setValue(String fieldName, String fieldValue) {
                cmbURLServer.setSelectedItem(fieldValue);
	}

	@Override
	public boolean showFavorites() {
		return false;
	}

	@Override
	public URL getIconURL() {
		return null;
	}

	@Override
	public String getInfoText() {
		return I18N.getString("orbisgis.org.orbisgis.wms.urlInput");
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
