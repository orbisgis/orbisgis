package org.orbisgis.core.crs;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class WKTTab extends JPanel {

	private JTextArea jTextArea;
	private String driverWKTHTML;

	private static final String NO_SRS_INFO = "MapContext contains no CRS text information";

	public WKTTab(String driverWKT) {
		init();
		setWKT(driverWKT);

	}

	private void init() {
		this.setLayout(new BorderLayout());
		jTextArea = new JTextArea();
		jTextArea.setText(driverWKTHTML);
                jTextArea.setLineWrap(true);
		Dimension pnlDimension = new Dimension(300, 300);
		JScrollPane scrollDriver = new JScrollPane(jTextArea);
		scrollDriver.setPreferredSize(pnlDimension);
		this.add(scrollDriver, BorderLayout.NORTH);

	}

	public void setWKT(String driverWKT) {
		if (driverWKT != null) {
			driverWKTHTML = "<html><pre>" + driverWKT + "</pre></html>";
		} else {
			driverWKTHTML = NO_SRS_INFO;
		}

		jTextArea.setText(driverWKTHTML);

	}

}
