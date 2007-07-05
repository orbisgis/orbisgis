package org.orbisgis.plugin.view.ui.workbench;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.orbisgis.plugin.sqlconsole.SQLConsole;
import org.orbisgis.plugin.view.layerModel.LayerCollection;

public class GeoView2DPanel extends JPanel {

	private MapControl mapControl;
	private TOC toc = null;

	public GeoView2DPanel(LayerCollection root) {
		mapControl = new MapControl();
		OGMapControlModel mapControlModel = new OGMapControlModel(root);
		mapControlModel.setMapControl(mapControl);
		mapControl.setMapControlModel(mapControlModel);

		final JSplitPane rootSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rootSplitPane.setOneTouchExpandable(true);
		final JSplitPane tocViewSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		tocViewSplitPane.setOneTouchExpandable(true);
		JScrollPane tocScrollPane = new JScrollPane(toc=new TOC(root));
		tocViewSplitPane.setLeftComponent(tocScrollPane);
		tocViewSplitPane.setRightComponent(mapControl);
		tocViewSplitPane.setDividerLocation(150);
		rootSplitPane.setLeftComponent(tocViewSplitPane);
		rootSplitPane.setRightComponent(new SQLConsole());
		rootSplitPane.setResizeWeight(1.0);

		this.setLayout(new BorderLayout());
		this.add(rootSplitPane, BorderLayout.CENTER);
	}


	public MapControl getMapControl() {
		return mapControl;
	}

	/** Retrieves the toc */
	public TOC getTOC() {
		return toc;
	}

}