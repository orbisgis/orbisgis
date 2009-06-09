package org.orbisgis.core.ui.views.geocatalog.newSourceWizards.wms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.orbisgis.sif.UIPanel;

public class SRSPanel extends JPanel implements UIPanel {

	private WMSConnectionPanel wmsConnection;
	private JList lstSRS;

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
		lstSRS = new JList(wmsConnection.getWMSClient().getRootLayer()
				.getAllSrs().toArray());
		lstSRS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(lstSRS);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);

		return null;
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
