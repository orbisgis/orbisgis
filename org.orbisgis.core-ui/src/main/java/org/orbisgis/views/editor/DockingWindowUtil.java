package org.orbisgis.views.editor;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

public class DockingWindowUtil {

	public static DockingWindow findWindow(DockingWindow wnd,
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

	public static void addNewView(RootWindow root, View dockingView) {
		TabWindow tab = (TabWindow) DockingWindowUtil.findWindow(root,
				TabWindow.class);
		if (tab != null) {
			tab.addTab(dockingView);
		} else {
			View view = (View) DockingWindowUtil.findWindow(root, View.class);
			if (view == null) {
				root.setWindow(dockingView);
			} else {
				DockingWindow parent = view.getWindowParent();
				tab = new TabWindow();
				tab.addTab(view);
				tab.addTab(dockingView);
				parent.replaceChildWindow(view, tab);
			}
		}
	}

}
