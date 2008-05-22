/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/**
 *
 */
package org.orbisgis.view;

import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import org.orbisgis.PersistenceException;
import org.orbisgis.editor.EditorDecorator;
import org.orbisgis.editorView.IEditorView;
import org.orbisgis.views.editor.DockingWindowUtil;

public class ViewDecorator {
	private String id;
	private String title;
	private String icon;
	private IView view;
	private View dockingView;
	private Component component;
	private boolean editor;
	private String editorId;
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
	 * @param editorId
	 *            The associated editor
	 */
	public ViewDecorator(IView view, String id, String title, String icon,
			boolean editor, String editorId) {
		super();
		this.view = view;
		this.id = id;
		this.title = title;
		this.icon = icon;
		this.editor = editor;
		this.editorId = editorId;
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

	public void loadStatus(EditorDecorator activeEditor)
			throws PersistenceException {
		view.loadStatus();
		component = view.getComponent();
		dockingView = new View(title, getImageIcon(), component);
		editorChanged(activeEditor);
	}

	public void open(RootWindow root, EditorDecorator activeEditor) {
		if (dockingView == null) {
			component = view.getComponent();
			dockingView = new View(title, getImageIcon(), component);
			DockingWindowUtil.addNewView(root, dockingView);
		} else {
			if (!isOpen()) {
				getDockingView().restore();
				if (!isOpen()) {
					dockingView = null;
					open(root, activeEditor);
				}
			}
		}

		editorChanged(activeEditor);
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

	public boolean isEditor() {
		return editor;
	}

	/**
	 * Shows the view or a message depending on the editor's id passed as an
	 * argument
	 *
	 * @param editorId
	 */
	public void editorChanged(EditorDecorator editor) {
		if (this.editorId == null) {
			return;
		} else {
			if (dockingView != null) {
				if ((editor == null) || (!editor.getId().equals(this.editorId))) {
					if (activeComponent == null) {
						activeComponent = dockingView.getComponent();
					}
					dockingView.setComponent(new JLabel("View not available"));
				} else {
					if (activeComponent != null) {
						dockingView.setComponent(activeComponent);
						activeComponent = null;
					}
					((IEditorView) view).setEditor(editor.getEditor());
				}
			}
		}
	}

	public void editorClosed(String editorId) {
		if (this.editorId == null) {
			return;
		} else {
			if ((dockingView != null) && (this.editorId.equals(editorId))) {
				((IEditorView) view).editorViewDisabled();
			}
		}
	}

}