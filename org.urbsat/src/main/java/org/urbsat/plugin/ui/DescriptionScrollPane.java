package org.urbsat.plugin.ui;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.orbisgis.geoview.GeoView2D;

public class DescriptionScrollPane extends JScrollPane {

	public static JTextArea jTextArea;
	public static GeoView2D geoview;

	public DescriptionScrollPane(GeoView2D geoview) {
		DescriptionScrollPane.geoview = geoview;
		setViewportView(getJTextArea());
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);
			jTextArea.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			jTextArea.setEditable(false);
		}
		return jTextArea;
	}
}