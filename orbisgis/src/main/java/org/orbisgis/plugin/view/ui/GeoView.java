package org.orbisgis.plugin.view.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.orbisgis.plugin.view.layerModel.LayerCollection;

public class GeoView extends JPanel {

	private MapControl mapControl;

	public GeoView(LayerCollection root) {
		mapControl = new MapControl();
		OGMapControlModel mapControlModel = new OGMapControlModel(root);
		mapControlModel.setMapControl(mapControl);
		mapControl.setMapControlModel(mapControlModel);

		JSplitPane rootSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rootSplitPane.setOneTouchExpandable(true);
		JSplitPane tocViewSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		tocViewSplitPane.setOneTouchExpandable(true);
		tocViewSplitPane.setLeftComponent(new JScrollPane(new TOC(root)));
		tocViewSplitPane.setRightComponent(mapControl);
		rootSplitPane.setLeftComponent(tocViewSplitPane);
		rootSplitPane.setRightComponent(new JLabel("SQL Console"));
		this.setLayout(new BorderLayout());
		this.add(rootSplitPane, BorderLayout.CENTER);

		// load the root and docking windows...
		// ViewMap viewMap = new ViewMap();
		// viewMap.addView(0, new View("LayerManager", null, new JScrollPane(
		// new TOC(root))));
		// viewMap.addView(1, new View("ViewLayer", null, mapControl));
		// viewMap.addView(2, new View("Console", null, new JLabel("Console")));
		// viewMap.addView(3, new View("SQL Console", null, new JLabel(
		// "SQL Console")));
		//
		// RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		// rootWindow
		// .setWindow(new SplitWindow(false, 0.7f, new SplitWindow(true,
		// 0.2f, viewMap.getView(0), viewMap.getView(1)),
		// new TabWindow(new DockingWindow[] { viewMap.getView(2),
		// viewMap.getView(3) })));
		//
		// this.setLayout(new BorderLayout());
		// this.add(rootWindow, BorderLayout.CENTER);
		//
		// configureButtons(rootWindow);
	}
//
//	private void configureButtons(DockingWindow window) {
//		DockingWindowProperties props = window.getWindowProperties();
//		props.setCloseEnabled(false);
//		props.setDockEnabled(false);
//		props.setUndockEnabled(false);
//		for (int i = 0; i < window.getChildWindowCount(); i++) {
//			configureButtons(window.getChildWindow(i));
//		}
//	}

	public MapControl getMapControl() {
		return mapControl;
	}
}