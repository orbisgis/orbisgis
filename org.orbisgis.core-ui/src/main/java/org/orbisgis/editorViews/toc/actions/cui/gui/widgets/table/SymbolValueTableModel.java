package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.util.LinkedList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.gdms.data.values.AbstractValue;
import org.gdms.data.values.Value;
import org.orbisgis.renderer.legend.Symbol;

public class SymbolValueTableModel implements TableModel {

	
	public void addTableModelListener(TableModelListener arg0) {
		listeners.add(arg0);
	}
	
	public void removeAll(){
		data.removeAll(data);
	}

	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex)
        {
            case 0:
                return Symbol.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            default:
                return Object.class;
        }
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex)
        {
            case 0:
                return "Symbol";
            case 1:
                return "Value";
            case 2:
                return "Label";
            default:
                return null;
        }
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int colIndex) {
		SymbolValuePOJO aux;
		
		aux=(SymbolValuePOJO)data.get(rowIndex);
		
		switch(colIndex){
		case -1:
			return aux;
		case 0:
			return aux.getSym();
		case 1:
			return aux.getVal();
		case 2:
			return aux.getLabel();
		case 3:
			return aux.getValueType();
		default:
			return null;
		}
		
	}
	
	public void deleteSymbolValue(int row){
		data.remove(row);
		TableModelEvent event;
		event = new TableModelEvent(this, row, row,
					TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		callSubscriptors(event);
	}
	
	public void addSymbolValue(SymbolValuePOJO poj){
		data.add(poj);
		
		 TableModelEvent event;
	     event = new TableModelEvent (this, this.getRowCount()-1,
	            this.getRowCount()-1, TableModelEvent.ALL_COLUMNS,
	            TableModelEvent.INSERT);

	     callSubscriptors (event);
		
	}
	
	
	public boolean isCellEditable(int arg0, int arg1) {
		return true;
	}

	public void removeTableModelListener(TableModelListener arg0) {
		listeners.remove(arg0);

	}

	public void setValueAt(Object aValue, int rowIndex, int colIndex) {
		SymbolValuePOJO aux;
		aux=(SymbolValuePOJO)data.get(rowIndex);
		
		switch(colIndex){
		case 0:
			aux.setSym((Symbol)aValue);
			break;
		case 1:
			aux.setVal((String)aValue);
			break;
		case 2:
			aux.setLabel((String)aValue);
			break;
		default:
			break;
		}
		
		TableModelEvent event = new TableModelEvent (this, rowIndex, rowIndex, 
	            colIndex);

	    callSubscriptors(event);
	}
	
	private void callSubscriptors (TableModelEvent evento)
    {
        int i;
        
        for (i=0; i<listeners.size(); i++)
            ((TableModelListener)listeners.get(i)).tableChanged(evento);
    }
	
	public void setOrdered(boolean selected) {
		//setOrdered(selected);
		ordered=selected;
		if (selected){
			orderTable();
		}
	}
	
	public void deleteAllSymbols() {
		if (data.size()>0){
			int max_data = data.size()-1;
			while (data.size()>0){
				data.remove(0);
			}
			TableModelEvent event;
			event = new TableModelEvent(this, 0, max_data,
						TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
			callSubscriptors(event);
		}
	}
	
	private void orderTable() {
			
	}

	private LinkedList<SymbolValuePOJO> data = new LinkedList<SymbolValuePOJO>();
	private LinkedList<TableModelListener> listeners = new LinkedList<TableModelListener>();
	private boolean ordered=false;
	
	
}
