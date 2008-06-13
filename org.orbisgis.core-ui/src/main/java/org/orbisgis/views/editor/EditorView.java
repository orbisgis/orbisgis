/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.editor;

import java.awt.Component;
import java.util.ArrayList;

import org.orbisgis.Services;
import org.orbisgis.editor.EditorDecorator;
import org.orbisgis.editor.EditorListener;
import org.orbisgis.editor.IEditor;
import org.orbisgis.view.IEditorsView;

public class EditorView implements IEditorsView {

	private EditorPanel editor = new EditorPanel(this);
	private ArrayList<EditorListener> listeners = new ArrayList<EditorListener>();

	public void delete() {
	}

	public Component getComponent() {
		return editor;
	}

	public void initialize() {
		Services.registerService("org.orbisgis.EditorManager",
				EditorManager.class,
				"Gets access to the active editor and its document",
				new DefaultEditorManager(editor));
	}

	public void loadStatus() {

	}

	public void saveStatus() {
		editor.saveAllDocuments();
	}

	public static String getViewId() {
		return "org.orbisgis.views.EditorView";
	}

	public EditorDecorator getActiveEditor() {
		return editor.getCurrentEditor();
	}

	public void addEditorListener(EditorListener listener) {
		listeners.add(listener);
	}

	public void removeEditorListener(EditorListener listener) {
		listeners.remove(listener);
	}

	void fireActiveEditorChanged(IEditor previous, IEditor current) {
		for (EditorListener listener : listeners) {
			listener.activeEditorChanged(previous, current);
		}
	}

	public void fireEditorClosed(IEditor editor, String editorId) {
		for (EditorListener listener : listeners) {
			listener.activeEditorClosed(editor, editorId);
		}
	}

}
