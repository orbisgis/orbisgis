/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.editor;

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
