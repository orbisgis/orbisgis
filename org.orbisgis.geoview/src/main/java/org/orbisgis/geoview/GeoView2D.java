package org.orbisgis.geoview;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.orbisgis.core.ActionExtensionPointHelper;
import org.orbisgis.pluginManager.ExtensionPointManager;

public class GeoView2D extends JFrame {

	private MapControl map;

	private OGMapControlModel mapModel;

	public GeoView2D() {

		JToolBar navigationToolBar = new JToolBar();

		ActionListener al = new CustomActionListener();
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		ActionExtensionPointHelper.configureMenuAndToolBar(
				"org.orbisgis.geoview.Action", al, menuBar, navigationToolBar);
		this.setLayout(new BorderLayout());
		this.getContentPane().add(navigationToolBar, BorderLayout.PAGE_START);
		map = new MapControl();
		mapModel = new OGMapControlModel();
		mapModel.setMapControl((MapControl) map);
		((MapControl) map).setMapControlModel(mapModel);

		this.getContentPane().add(map, BorderLayout.CENTER);
		this.setTitle("OrbisGIS :: G e o V i e w 2D");
		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		this.setIconImage(new ImageIcon(url).getImage());

	}

	private class CustomActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			ExtensionPointManager<IGeoviewAction> epm = new ExtensionPointManager<IGeoviewAction>(
					"org.orbisgis.geoview.Action");
			IGeoviewAction action = epm.instantiateFrom(
					"/extension/action[@id='" + e.getActionCommand() + "']",
					"class");
			action.actionPerformed(GeoView2D.this);
		}
	}

	public OGMapControlModel getMapModel() {
		return mapModel;
	}

	public MapControl getMap() {
		return map;
	}
}