package org.orbisgis.plugin.view.ui.workbench;

import java.awt.BorderLayout;

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

		JSplitPane rootSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rootSplitPane.setOneTouchExpandable(true);
		JSplitPane tocViewSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		tocViewSplitPane.setOneTouchExpandable(true);
		tocViewSplitPane.setLeftComponent(new JScrollPane(toc=new TOC(root)));
		tocViewSplitPane.setRightComponent(mapControl);
		rootSplitPane.setLeftComponent(tocViewSplitPane);
		rootSplitPane.setRightComponent(new SQLConsole());
		
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