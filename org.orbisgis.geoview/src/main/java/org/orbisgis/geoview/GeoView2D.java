package org.orbisgis.geoview;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.ViewMap;

import org.orbisgis.core.ActionExtensionPointHelper;
import org.orbisgis.core.MenuTree;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class GeoView2D extends JFrame {

	private MapControl map;

	private OGMapControlModel mapModel;

	public GeoView2D() {

		JToolBar navigationToolBar = new JToolBar();

		ActionListener al = new CustomActionListener();
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		MenuTree menuTree = new MenuTree();
		ActionExtensionPointHelper.configureMenuAndToolBar(
				"org.orbisgis.geoview.Action", al, menuTree, navigationToolBar);
		JComponent[] menus = menuTree.getJMenus();
		for (int i = 0; i < menus.length; i++) {
			menuBar.add(menus[i]);
		}
		this.setLayout(new BorderLayout());
		this.getContentPane().add(navigationToolBar, BorderLayout.PAGE_START);
		map = new MapControl(new GeoViewEditionContext(this));
		mapModel = new OGMapControlModel();
		mapModel.setMapControl((MapControl) map);
		((MapControl) map).setMapControlModel(mapModel);
		this.setTitle("OrbisGIS :: G e o V i e w 2D");
		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		this.setIconImage(new ImageIcon(url).getImage());

		View mapControlView = new View("Map", null, map);
		ViewMap viewMap = new ViewMap();
		viewMap.addView(0, mapControlView);

		View[] extensionViews = getExtensionViews();
		TabWindow extensionTab = new TabWindow();
		for (int i = 0; i < extensionViews.length; i++) {
			viewMap.addView(i + 1, extensionViews[i]);
			extensionTab.addTab(extensionViews[i]);
		}
		RootWindow root = new RootWindow(null);
		SplitWindow splitWindow = new SplitWindow(true, 0.3f, extensionTab,
				mapControlView);
		root.setWindow(splitWindow);
		this.getContentPane().add(root, BorderLayout.CENTER);
	}

	private View[] getExtensionViews() {
		ArrayList<View> ret = new ArrayList<View>();
		ExtensionPointManager<IView> epm = new ExtensionPointManager<IView>(
				"org.orbisgis.geoview.View");
		ArrayList<ItemAttributes<IView>> views = epm
				.getItemAttributes("/extension/view");
		for (ItemAttributes<IView> itemAttributes : views) {
			String iconStr = itemAttributes.getAttribute("icon");
			Icon icon = null;
			if (iconStr != null) {
				icon = new ImageIcon(getClass().getResource(iconStr));
			}
			String title = itemAttributes.getAttribute("title");
			IView view = itemAttributes.getInstance("class");
			View idwView = new View(title, icon, view.getComponent(this));
			ret.add(idwView);
		}

		return ret.toArray(new View[0]);
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