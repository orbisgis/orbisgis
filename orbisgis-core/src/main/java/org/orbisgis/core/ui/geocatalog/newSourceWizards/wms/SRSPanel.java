package org.orbisgis.core.ui.geocatalog.newSourceWizards.wms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.components.text.JTextFilter;

public class SRSPanel extends JPanel implements UIPanel {

	private WMSConnectionPanel wmsConnection;
	private JList lstSRS;
	private JTextFilter txtFilter;
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
		return "Choose the appropriate SRS";
	}

	@Override
	public String getTitle() {
		return "SRS selection";
	}

	@Override
	public String initialize() {

		this.setLayout(new BorderLayout());
		if (null == scrollPane){
		scrollPane = new JScrollPane(getListSRS());
		}
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(getSearchSRSPanel(), BorderLayout.NORTH);

		return null;
	}

	public JList getListSRS() {
		if (null == lstSRS) {
			lstSRS = new JList();
			SRSlistModel = new SRSListModel(wmsConnection.getWMSClient()
					.getRootLayer().getAllSrs());
			lstSRS.setModel(SRSlistModel);
			lstSRS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return lstSRS;
	}

	public JPanel getSearchSRSPanel() {

		if (null == searchPanel) {
			searchPanel = new JPanel();
			JLabel label = new JLabel("Search a SRS code : ");

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

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String validateInput() {
		if (lstSRS.getSelectedIndex() == -1) {
			return "A SRS must be selected";
		}
		return null;
	}

	public String getSRS() {
		return lstSRS.getSelectedValue().toString();
	}

}
