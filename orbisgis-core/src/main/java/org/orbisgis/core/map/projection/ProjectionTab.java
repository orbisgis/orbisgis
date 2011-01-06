/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.map.projection;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms.SRSListModel;

import fr.cts.util.CRSUtil;

public class ProjectionTab extends JPanel {

	private JPanel searchPanel;
	private JButtonTextField txtFilter;
	private SRSListModel SRSlistModel;
	private JList lstSRS;
	private JScrollPane scrollPane;

	public ProjectionTab() {
		initUI();
	}

	private void initUI() {
		this.setLayout(new BorderLayout());
		if (null == scrollPane) {
			scrollPane = new JScrollPane(getListSRS());
		}
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(getSearchSRSPanel(), BorderLayout.NORTH);
	}

	public JList getListSRS() {
		if (null == lstSRS) {
			lstSRS = new JList();
			SRSlistModel = new SRSListModel(CRSUtil.getCRSFactory()
					.getAvailableCRSCode().toArray(new String[0]));
			lstSRS.setModel(SRSlistModel);
			lstSRS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lstSRS.setSelectedIndex(0);
		}
		return lstSRS;
	}

	public JPanel getSearchSRSPanel() {

		if (null == searchPanel) {
			searchPanel = new JPanel();
			JLabel label = new JLabel("Search a code : ");

			txtFilter = new JButtonTextField(8);
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

	public String getSRS() {
		return lstSRS.getSelectedValue().toString();
	}

}
