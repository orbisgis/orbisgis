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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;

public class SymbolListModel implements ListModel {

	private ArrayList<ISymbolEditor> editors = new ArrayList<ISymbolEditor>();
	private ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();

	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public Object getElementAt(int index) {
		return editors.get(index).getSymbol().getName();
	}

	public int getSize() {
		return editors.size();
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public void removeAll() {
		editors.clear();
		refresh();
	}

	public void addElement(Symbol symbol, ISymbolEditor editor) {
		editor.setSymbol(symbol);
		editors.add(editor);
		refresh();
	}

	public Symbol getSymbol(int i) {
		return editors.get(i).getSymbol();
	}

	public void moveUp(int idx) {
		editors.add(idx - 1, editors.remove(idx));
		refresh();
	}

	public ISymbolEditor getEditor(int index) {
		return editors.get(index);
	}

	public void moveDown(int idx) {
		editors.add(idx + 1, editors.remove(idx));
		refresh();
	}

	public void removeElementAt(int i) {
		editors.remove(i);
		refresh();
	}

	public void refresh() {
		for (ListDataListener listener : listeners) {
			listener.contentsChanged(new ListDataEvent(this,
					ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
		}
	}

}