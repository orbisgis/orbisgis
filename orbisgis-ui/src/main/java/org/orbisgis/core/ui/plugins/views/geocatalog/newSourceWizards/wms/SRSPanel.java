/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
import java.net.URL;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.utils.I18N;

public class SRSPanel extends JPanel implements UIPanel {

	private WMSConnectionPanel wmsConnection;
	private JList lstSRS;
	private JButtonTextField txtFilter;
	private SRSListModel SRSlistModel;
	private JPanel searchPanel;
	private Component scrollPane;

	public SRSPanel(WMSConnectionPanel wmsConnection) {
		this.wmsConnection = wmsConnection;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public URL getIconURL() {
		return null;
	}

	@Override
	public String getInfoText() {
		return "";
	}

	@Override
	public String getTitle() {
		return I18N.getString("orbisgis.org.orbisgis.wms.SRSSelection");
	}

	@Override
	public String initialize() {

		this.setLayout(new BorderLayout());
		if (null == scrollPane) {
			scrollPane = new JScrollPane(getListSRS());
		}
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(getSearchSRSPanel(), BorderLayout.NORTH);

		return null;
	}

	public JList getListSRS() {
		if (null == lstSRS) {
			lstSRS = new JList();
			Vector allSrs = wmsConnection.getWMSClient().getRootLayer()
					.getAllSrs();
			String[] srsNames = new String[allSrs.size()];
			allSrs.toArray(srsNames);
			SRSlistModel = new SRSListModel(srsNames);
			lstSRS.setModel(SRSlistModel);
			lstSRS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return lstSRS;
	}

	public JPanel getSearchSRSPanel() {

		if (null == searchPanel) {
			searchPanel = new JPanel();
			JLabel label = new JLabel(I18N
					.getString("orbisgis.org.orbisgis.wms.SearchSRSCode"));

			txtFilter = new JButtonTextField();
			txtFilter.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					doFilter();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					doFilter();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					doFilter();
				}
			});
			searchPanel.add(label);
			searchPanel.add(txtFilter);
		}
		return searchPanel;

	}

	private void doFilter() {
		SRSlistModel.filter(txtFilter.getText());
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String validateInput() {
		if (lstSRS.getSelectedIndex() == -1) {
			return I18N.getString("orbisgis.org.orbisgis.wms.ChooseSRSCode");
		}
		return null;
	}

	public String getSRS() {
		return lstSRS.getSelectedValue().toString();
	}

}
