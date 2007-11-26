/**
 *
 */
package org.orbisgis.geoview;

import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

class ViewDecorator {
	private String id;
	private String title;
	private String icon;
	private IView view;
	private View dockingView;
	private GeoView2D geoview;
	private Component component;

	public ViewDecorator(IView view, String id, String name, String icon,
			GeoView2D geoview) {
		super();
		this.view = view;
		this.id = id;
		this.title = name;
		this.icon = icon;
		this.geoview = geoview;
	}

	public IView getView() {
		return view;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getIcon() {
		return icon;
	}

	public View getDockingView() {
		return dockingView;
	}

	public void close() {
		if (isOpen()) {
			dockingView.close();
		}
	}

	public void open(RootWindow root) {
		if (dockingView == null) {
			component = view.getComponent(geoview);
			dockingView = new View(title, getImageIcon(), component);
			TabWindow tab = (TabWindow) findWindow(root, TabWindow.class);
			if (tab != null) {
				tab.addTab(dockingView);
			} else {
				View view = (View) findWindow(root, View.class);
				DockingWindow parent = view.getWindowParent();
				tab = new TabWindow();
				tab.addTab(view);
				tab.addTab(dockingView);
				parent.replaceChildWindow(view, tab);
			}
		} else {
			if (!isOpen()) {
				getDockingView().restore();
			}
		}
	}

	private DockingWindow findWindow(DockingWindow wnd,
			Class<? extends DockingWindow> clazz) {
		if (wnd.getClass().equals(clazz)) {
			return wnd;
		} else {
			for (int i = 0; i < wnd.getChildWindowCount(); i++) {
				DockingWindow ret = findWindow(wnd.getChildWindow(i), clazz);
				if (ret != null) {
					return ret;
				}
			}

			return null;
		}
	}

	private Icon getImageIcon() {
		if (icon != null) {
			URL url = ViewDecorator.class.getResource(icon);
			if (url != null) {
				return new ImageIcon(url);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Component getViewComponent() {
		return component;
	}

	public boolean isOpen() {
		if (dockingView == null) {
			return false;
		} else {
			return dockingView.getWindowParent() != null;
		}
	}

}