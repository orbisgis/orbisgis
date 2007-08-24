package org.orbisgis.plugin.view3d;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.orbisgis.plugin.sqlconsole.SQLConsole;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.ui.workbench.TOC;

/**
 * This is the main panel of the 3D viewer. It contains a table of contents
 * (TOC) and a canvas3D (MapControl3D). TODO : This code is almost the same as
 * GeoView2DPanel : we should use a common class...
 * 
 * @author Samuel CHEMLA
 * 
 */
public class GeoView3DPanel extends JPanel {

	// MapControl will contain the canvas
	private MapControl3D mapControl3D = null;

	// The TOC is used to manage the layer system
	private TOC toc = null;

	// Add a SQL console
	private SQLConsole sqlConsole = null;

	/**
	 * Constructor used to initialize the fields and create a nice panel
	 * 
	 * @param root
	 */
	public GeoView3DPanel(LayerCollection root) {
		mapControl3D = new MapControl3D();
		toc = new TOC(root);
		sqlConsole = new SQLConsole();

		/*
		 * We will have to be very careful with split panes and the map control.
		 * Indeed the map control contains an heavyweight component... TODO :
		 * test if there are any display bugs
		 */
		final JSplitPane rootSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT);
		rootSplitPane.setOneTouchExpandable(true);
		final JSplitPane tocViewSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		tocViewSplitPane.setOneTouchExpandable(true);
		JScrollPane tocScrollPane = new JScrollPane(toc);
		tocViewSplitPane.setLeftComponent(tocScrollPane);
		tocViewSplitPane.setRightComponent(mapControl3D);
		tocViewSplitPane.setDividerLocation(150);
		rootSplitPane.setLeftComponent(tocViewSplitPane);
		rootSplitPane.setRightComponent(sqlConsole);
		rootSplitPane.setResizeWeight(1.0);

		this.setLayout(new BorderLayout());
		this.add(rootSplitPane, BorderLayout.CENTER);

	}

}
