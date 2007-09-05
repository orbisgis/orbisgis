package org.orbisgis.plugin.view.ui.workbench;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.orbisgis.plugin.sqlconsole.SQLConsole;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view3d.MapControl3D;

public class GeoView2DPanel extends JPanel {

	private JComponent map = null;

	private TOC toc = null;

	private SQLConsole sqlConsole = null;

	/**
	 * This constructor is used for compatibility with GeoView2D
	 * 
	 * @param root
	 */
	public GeoView2DPanel(LayerCollection root) {
		this(root, false);
	}

	/**
	 * This constructor creates a Panel with a TOC, a SQLConsole and a viewer.
	 * The viewer can be 3D or 2D according to the params.
	 * 
	 * @param root
	 * @param is3D
	 *            true is we create a 3D viewer, false for 2D
	 */
	public GeoView2DPanel(LayerCollection root, boolean is3D) {
		if (is3D) {
			map = new MapControl3D();
			toc = new TOC(root, true);
		} else {
			map = new MapControl();
			toc = new TOC(root);
			OGMapControlModel mapControlModel = new OGMapControlModel(root);
			mapControlModel.setMapControl((MapControl) map);
			((MapControl) map).setMapControlModel(mapControlModel);
		}
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
	
	public MapControl3D getMapControl3D() {
		return (MapControl3D) map;
	}

	/** Retrieves the toc */
	public TOC getTOC() {
		return toc;
	}

}