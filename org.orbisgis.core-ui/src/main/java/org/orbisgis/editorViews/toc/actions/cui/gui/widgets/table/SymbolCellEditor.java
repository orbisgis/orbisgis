package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.FlowLayoutPreviewWindow;
import org.orbisgis.renderer.legend.Symbol;
import org.sif.UIFactory;

public class SymbolCellEditor extends ButtonCanvas implements TableCellEditor {
	

	private static final long serialVersionUID = -4827452306234308388L;
		
	public SymbolCellEditor(){
		super();
	}
	
	public Component getTableCellEditorComponent(JTable arg0, Object arg1,
			boolean arg2, int arg3, int arg4) {
		
		Symbol symb = (Symbol) arg1;
		setLegend(symb, getConstraint(symb));
		
		return this;
	}

	public void addCellEditorListener(CellEditorListener arg0) {
		listeners.add(arg0);
	}

	public void cancelCellEditing() {
		
	}

	public Object getCellEditorValue() {
		return s;
	}

	public boolean isCellEditable(EventObject arg0) {
		return true;
	}

	public void removeCellEditorListener(CellEditorListener arg0) {
		listeners.remove(arg0);
	}

	public boolean shouldSelectCell(EventObject arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean stopCellEditing() {
		return false;
	}
		
	
	private LinkedList<CellEditorListener> listeners = new LinkedList<CellEditorListener>();

}
