package org.urbsat.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.orbisgis.geoview.GeoView2D;

public class UrbSATPanel extends JPanel {

	private JScrollPane jScrollPane2;

	private JSplitPane splitPanel;

	private GeoView2D geoview;

	/**
	 * This is the default constructor
	 * 
	 * @param geoview
	 */
	public UrbSATPanel(GeoView2D geoview) {
		this.geoview = geoview;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getSplitPane(), BorderLayout.CENTER);
	}

	private Component getSplitPane() {
		if (splitPanel == null) {
			splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			splitPanel.setLeftComponent(getJScrollPaneEast());
			splitPanel.setRightComponent(new DescriptionScrollPane(geoview));
			splitPanel.setOneTouchExpandable(true);
			splitPanel.setResizeWeight(1);
			splitPanel.setContinuousLayout(true);
			splitPanel.setPreferredSize(new Dimension(400, 140));
		}
		return splitPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneEast() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(new FunctionsPanel(geoview));
			jScrollPane2.setPreferredSize(new Dimension(150, 250));
		}
		return jScrollPane2;
	}
}