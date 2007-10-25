package org.orbisgis.geoview;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.orbisgis.core.Menu;
import org.orbisgis.core.MenuTree;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class GeoView2D extends JFrame {

	private MapControl map;

	private OGMapControlModel mapModel;

	public GeoView2D() {

		// Action zoomFullAction = new CustomAction("Zoom full", "zoomFull.png",
		// ZOOM_FULL);
		// Action zoomInAction = new CustomAction("Zoom in", "zoomIn.png",
		// ZOOM_IN);
		// Action zoomOutAction = new CustomAction("Zoom out", "zoomOut.png",
		// ZOOM_OUT);
		// Action panAction = new CustomAction("Zoom in", "pan.png", PAN);
		// Action featureInfo = new CustomAction("Feature info",
		// "featureInfo.png", FEATUREINFO);
		//
		// Action openAttributes = new CustomAction("Open attributes",
		// "openattributes.png", OPENATTRIBUTES);

		// TODO add these actions
		// JMenuBar menuBar = new JMenuBar();
		// JMenu file = new JMenu("File");
		// JMenuItem addSpatialDS = new JMenuItem(openAction);
		// file.add(addSpatialDS);
		// file.addSeparator();
		// JMenuItem exit = new JMenuItem(exitAction);
		// file.add(exit);
		// menuBar.add(file);
		//
		// JMenu help = new JMenu("Help");
		// JMenuItem about = new JMenuItem("About");
		// about.setIcon(new
		// ImageIcon(this.getClass().getResource("about.png")));
		// help.add(about);
		// menuBar.add(help);

		JToolBar navigationToolBar = new JToolBar();
		// navigationToolBar.add(openAction);
		// navigationToolBar.add(exitAction);
		//
		// navigationToolBar.add(zoomFullAction);
		// navigationToolBar.add(zoomInAction);
		// navigationToolBar.add(zoomOutAction);
		// navigationToolBar.add(panAction);
		// navigationToolBar.add(featureInfo);
		// navigationToolBar.add(openAttributes);

		ActionListener al = new CustomActionListener();
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		configureMenuBar(al, menuBar, navigationToolBar);
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

	private void configureMenuBar(ActionListener al, JMenuBar menuBar,
			JToolBar navigationToolBar) {

		HashMap<String, JToolBar> idToolBar = new HashMap<String, JToolBar>();
		ArrayList<String> orderedToolBarIds = new ArrayList<String>();

		HashMap<String, ButtonGroup> exclusiveGroups = new HashMap<String, ButtonGroup>();

		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions("org.orbisgis.geoview.Action");
		MenuTree menuTree = new MenuTree(al);
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/menu)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/menu[" + (i + 1) + "]";
				String parent = c.getAttribute(base, "parent");
				String id = c.getAttribute(base, "id");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Menu m = new Menu(parent, id, text, icon);
				menuTree.addMenu(m);
			}
		}
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/toolbar)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/toolbar[" + (i + 1) + "]";
				String id = c.getAttribute(base, "id");
				String text = c.getAttribute(base, "text");
				idToolBar.put(id, new JToolBar(text));
				orderedToolBarIds.add(id);
			}
		}
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/action)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/action[" + (i + 1) + "]";
				String parent = c.getAttribute(base, "parent");
				String id = c.getAttribute(base, "id");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Menu menu = new Menu(parent, id, text, icon);
				menuTree.addMenu(menu);

				if (icon != null) {
					String toolBarId = c.getAttribute(base, "toolbarId");
					if (toolBarId != null) {
						JToolBar toolBar = idToolBar.get(toolBarId);
						if (toolBar == null) {
							throw new RuntimeException("Cannot find toolbar: "
									+ toolBarId + ". Extension: "
									+ exts[j].getId());
						}
						AbstractButton btn;
						String exclusiveGroup = c.getAttribute(base,
								"exclusiveGroup");
						if (exclusiveGroup != null) {
							ButtonGroup bg = exclusiveGroups
									.get(exclusiveGroup);
							if (bg == null) {
								bg = new ButtonGroup();
								exclusiveGroups.put(exclusiveGroup, bg);
							}
							btn = new JToggleButton(new ImageIcon(this.getClass()
									.getResource(icon)), false);
							menu.setRelatedToggleButton((JToggleButton) btn);
							bg.add(btn);
						} else {
							btn = new JButton(new ImageIcon(this.getClass()
									.getResource(icon)));
						}
						btn.setActionCommand(id);
						btn.addActionListener(al);
						toolBar.add(btn);
					}
				}
			}
		}
		JMenuItem[] menus = menuTree.getJMenus();
		for (int i = 0; i < menus.length; i++) {
			menuBar.add(menus[i]);
		}
		for (String toolBarId : orderedToolBarIds) {
			JToolBar toolbar = idToolBar.get(toolBarId);
			navigationToolBar.add(toolbar);
		}
	}

	private class CustomActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			ExtensionPointManager<IGeoviewAction> epm = new ExtensionPointManager<IGeoviewAction>(
					"org.orbisgis.geoview.Action");
			IGeoviewAction action = epm.instantiateFrom(
					"/extension/action[@id='" + e.getActionCommand() + "']",
					"class");
			action.actionPerformed(GeoView2D.this);

			// } else if (PAN.equals(e.getActionCommand())) {
			// try {
			// map.setTool(new PanTool());
			// } catch (TransitionException e1) {
			// throw new RuntimeException(e1);
			// }
			// } else if (FEATUREINFO.equals(e.getActionCommand())) {
			// } else if (OPENATTRIBUTES.equals(e.getActionCommand())) {
			// // try {
			// // OrbisgisCore.getDSF()
			// // .executeSQL("call show('select * from "
			// // + mapModel.getSelectedLayers().getName() + "','"
			// // + TOC.selectedLayer.getName() + "');");
			// // } catch (SyntaxException e1) {
			// // // TODO Auto-generated catch block
			// // e1.printStackTrace();
			// // } catch (DriverLoadException e1) {
			// // // TODO Auto-generated catch block
			// // e1.printStackTrace();
			// // } catch (NoSuchTableException e1) {
			// // // TODO Auto-generated catch block
			// // e1.printStackTrace();
			// // } catch (ExecutionException e1) {
			// // // TODO Auto-generated catch block
			// // e1.printStackTrace();
			// // }
			// }
		}
	}

	public OGMapControlModel getMapModel() {
		return mapModel;
	}

	public MapControl getMap() {
		return map;
	}
}