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