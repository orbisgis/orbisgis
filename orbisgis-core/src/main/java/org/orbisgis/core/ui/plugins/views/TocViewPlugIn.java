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
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.views;

import java.awt.Component;

import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.toc.AbstractTableEditableElement;
import org.orbisgis.core.ui.editorViews.toc.Toc;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class TocViewPlugIn extends ViewPlugIn {

	private Toc panel;
	private String editors[];
	private JMenuItem menuItem;
	private EditorListener closingListener = null;

	public Toc getPanel() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		editors = new String[2];
		editors[0] = Names.EDITOR_MAP_ID;
		editors[1] = Names.EDITOR_TABLE_ID;

		panel = new Toc();
		// setComponent(panel,"Memory", getIcon("utilities-system-monitor.png"),
		// context);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.TOC, true, OrbisGISIcon.MAP,
				editors, panel, context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public boolean setEditor(IEditor editor) {
		listenEditorClosing();
		EditableElement element = editor.getElement();
		if (editor instanceof MapEditorPlugIn) {
			panel.setMapContext(element, (MapEditorPlugIn) editor);
			return true;
		} else if (editor instanceof TableEditorPlugIn) {
			TableEditableElement tableElement = (TableEditableElement) element;
			MapContext mapContext = tableElement.getMapContext();
			// Get MapContext element
			EditorManager em = Services.getService(EditorManager.class);
			IEditor[] mapEditors = em.getEditors(Names.EDITOR_MAP_ID,
					mapContext);
			if (mapEditors.length > 0) {
				panel.setMapContext(mapEditors[0].getElement());
				return true;
			} else {
				editorViewDisabled();
				return false;
			}
		}

		return false;
	}

	public void listenEditorClosing() {
		if (closingListener == null) {
			closingListener = new EditorListener() {

				@Override
				public boolean activeEditorClosing(IEditor editor,
						String editorId) {
					if (editorId.equals(Names.EDITOR_MAP_ID)) {
						MapContext mc = (MapContext) editor.getElement()
								.getObject();
						EditorManager em = Services
								.getService(EditorManager.class);
						IEditor[] editors = em
								.getEditors(Names.EDITOR_TABLE_ID);
						for (IEditor openEditor : editors) {
							AbstractTableEditableElement el = (AbstractTableEditableElement) openEditor
									.getElement();
							if (el.getMapContext() == mc) {
								if (!em.closeEditor(openEditor)) {
									return false;
								}
							}
						}
					}

					return true;
				}

				@Override
				public void activeEditorClosed(IEditor editor, String editorId) {
				}

				@Override
				public void activeEditorChanged(IEditor previous,
						IEditor current) {
				}

				@Override
				public void elementLoaded(IEditor editor, Component comp) {
				}

			};
			EditorManager em = Services.getService(EditorManager.class);
			em.addEditorListener(closingListener);
		}
	}

	public void editorViewDisabled() {
		// TODO (pyf): TOC is not available after one editor is closed. Except
		// that if it stays only one editor in EditorPanel.
		// In this case Toc context is updated. But upper 2 editors the previous
		// editor is not updated
		panel.setMapContext(null);
		panel.setMapContext(null, null);
	}

	public void delete() {
		panel.delete();
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isSelected() {
		boolean isSelected = false;
		isSelected = getPlugInContext().viewIsOpen(getId());
		menuItem.setSelected(isSelected);
		return isSelected;
	}

	public String getName() {
		return "Toc view";
	}
}
