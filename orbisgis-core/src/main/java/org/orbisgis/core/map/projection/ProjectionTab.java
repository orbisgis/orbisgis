package org.orbisgis.core.map.projection;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.core.ui.components.text.JTextFilter;
import org.orbisgis.core.ui.geocatalog.newSourceWizards.wms.SRSListModel;

import fr.cts.util.CRSUtil;

public class ProjectionTab extends JPanel {

	private JPanel searchPanel;
	private JTextFilter txtFilter;
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

			txtFilter = new JTextFilter();
			txtFilter.addDocumentListener(new DocumentListener() {

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
