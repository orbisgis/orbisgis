/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY, Adelin PIAU
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.DockingWindowUtil;
import org.orbisgis.utils.I18N;

public class ViewDecorator {
	private String id = "null";
	// private String title;
	private ImageIcon icon;
	private ViewPlugIn view;
	private View dockingView;
	private Component component;
	private String[] editors;
	private Component activeComponent = null;

	/**
	 * @param view
	 *            object to decorate
	 * @param id
	 *            id of the view extension
	 * @param title
	 *            Title to show in the panel
	 * @param icon
	 *            Icon to show in the panel
	 * @param editor
	 *            If this view is the one that contains the editors
	 * @param editors
	 *            The associated editors
	 */
	public ViewDecorator(ViewPlugIn view, String id, ImageIcon icon,
			String[] editors) {
		super();
		this.view = view;
		this.id = id;
		// this.title = title;
		this.icon = icon;
		this.editors = editors;
	}

	public ViewPlugIn getView() {
		return view;
	}

	public String getId() {
		return id;
	}

	public ImageIcon getIcon() {
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

	public void loadStatus(IEditor activeEditor, String activeEditorId) {
		try {
			view.loadStatus();
		} catch (PersistenceException e) {
			ErrorMessages.error(ErrorMessages.CannotRecoverPreviousStatusOfView
					+ " " + getId(), e);
		}
		component = view.getComponent();
		dockingView = new View(id, icon, component);
		editorChanged(activeEditor, activeEditorId);
	}

	public void open(RootWindow root, IEditor activeEditor,
			String activeEditorId) {
		if (dockingView == null) {
			component = view.getComponent();
			dockingView = new View(id, icon, component);
			DockingWindowUtil.addNewView(root, dockingView);
		} else {
			if (!isOpen()) {
				getDockingView().restore();
				if (!isOpen()) {
					dockingView = null;
					open(root, activeEditor, activeEditorId);
				}
			} else {
				dockingView.makeVisible();
			}
		}

		editorChanged(activeEditor, activeEditorId);
	}

	public Component getViewComponent() {
		return component;
	}

	public boolean isOpen() {
		if (dockingView == null) {
			return false;
		} else {
			return getParent(dockingView) instanceof RootWindow;
		}
	}

	private DockingWindow getParent(DockingWindow window) {
		DockingWindow parent = window.getWindowParent();
		if (parent == null) {
			return window;
		} else {
			return getParent(parent);
		}
	}

	/**
	 * Shows the view or a message depending on the editor's id passed as an
	 * argument
	 * 
	 * @param editorId
	 */
	public void editorChanged(IEditor editor, String editorId) {
		if (this.editors.length == 0) {
			return;
		} else {
			if (dockingView != null) {
				if ((editor == null) || (!isAssociatedEditor(editorId))) {
					disableView();
				} else {
					if (activeComponent != null) {
						dockingView.setComponent(activeComponent);
						activeComponent = null;
						dockingView.repaint();

					}
					if (!((ViewPlugIn) view).setEditor(editor)) {
						disableView();
					}
				}
			}
		}
	}

	private void disableView() {
		if (activeComponent == null) {
			activeComponent = dockingView.getComponent();
		}
		dockingView.setComponent(new JLabel(I18N
				.getText("orbisgis.errorMessages.viewNotAvailable")));
	}

	public void editorClosed(String editorId) {
		WorkbenchContext wbContext = Services
				.getService(WorkbenchContext.class);
		wbContext.setLastAction("Editor closed");
		if (this.editors.length == 0) {
			return;
		} else {
			if ((dockingView != null) && isAssociatedEditor(editorId)) {
				((ViewPlugIn) view).editorViewDisabled();

			}
		}
	}

	private boolean isAssociatedEditor(String editorId) {
		for (String editor : editors) {
			if (editor.equals(editorId)) {
				return true;
			}
		}
		return false;
	}

}