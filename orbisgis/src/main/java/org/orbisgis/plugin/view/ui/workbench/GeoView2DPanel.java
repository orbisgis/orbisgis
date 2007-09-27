package org.orbisgis.plugin.view.ui.workbench;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.orbisgis.plugin.sqlconsole.SQLConsole;
import org.orbisgis.plugin.view.layerModel.LayerCollection;

public class GeoView2DPanel extends JPanel {

	private JComponent map = null;

	private TOC toc = null;

	private SQLConsole sqlConsole = null;

	/**
	 * This constructor creates a Panel with a TOC, a SQLConsole and a viewer.
	 * 
	 * @param root
	 */
	public GeoView2DPanel(LayerCollection root) {
		map = new MapControl();
		toc = new TOC(root);
		OGMapControlModel mapControlModel = new OGMapControlModel(root);
		mapControlModel.setMapControl((MapControl) map);
		((MapControl) map).setMapControlModel(mapControlModel);
		sqlConsole = new SQLConsole();
		Initialize();
	}

	private void Initialize() {

		final JSplitPane rootSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT);
		rootSplitPane.setOneTouchExpandable(true);
		final JSplitPane tocViewSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		tocViewSplitPane.setOneTouchExpandable(true);

		JScrollPane tocScrollPane = new JScrollPane(toc);
		tocViewSplitPane.setLeftComponent(tocScrollPane);
		tocViewSplitPane.setRightComponent(map);
		tocViewSplitPane.setDividerLocation(150);
		rootSplitPane.setLeftComponent(tocViewSplitPane);
		rootSplitPane.setRightComponent(sqlConsole);
		rootSplitPane.setResizeWeight(1.0);

		this.setLayout(new BorderLayout());
		this.add(rootSplitPane, BorderLayout.CENTER);
	}

	public MapControl getMapControl() {
		return (MapControl) map;
	}

	/** Retrieves the toc */
	public TOC getTOC() {
		return toc;
	}
}