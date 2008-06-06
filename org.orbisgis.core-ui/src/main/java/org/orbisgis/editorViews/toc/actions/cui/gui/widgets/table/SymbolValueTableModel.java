package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.gdms.data.values.AbstractValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.renderer.legend.Symbol;

public class SymbolValueTableModel implements TableModel {

	
	public void addTableModelListener(TableModelListener arg0) {
		listeners.add(arg0);
	}
	
	public void removeAll(){
		data.removeAll(data);
		setOrdered(ordered);
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
		
		if (ordered){
			aux = (SymbolValuePOJO)data.get(dataOrder[rowIndex].intValue());
		}else{
			aux=(SymbolValuePOJO)data.get(rowIndex);
		}
		
		switch(colIndex){
		case -1:
			return aux;
		case 0:
			return aux.getSym();
		case 1:
			return aux.getVal().toString();
		case 2:
			return aux.getLabel();
		case 3:
			return aux.getVal();
		default:
			return null;
		}
		
	}
	
	public void deleteSymbolPojos(SymbolValuePOJO[] pojos) {
		for (int i=0; i<pojos.length; i++){
			deleteSymbolPojo(pojos[i]);
		}
	}
	
	public void deleteSymbolPojo(SymbolValuePOJO pojo) {
		int index=0;
		for (int i=0; i<data.size(); i++){
			if (data.get(i).equals(pojo)){
				if (ordered){
					for (int j=0; j<dataOrder.length; j++){
						if (dataOrder[j].intValue()==i){
							index=j;
							break;
						}
					}
				}else{
					index=i;
				}
				break;
			}
		}
		
		deleteSymbolValue(index);
		
	}
	
	public void deleteSymbolValue(int row){
		if (ordered){
			data.remove(dataOrder[row].intValue());
		}else{
			data.remove(row);
		}
		
		TableModelEvent event;
		if (ordered){
			event = new TableModelEvent(this, dataOrder[row].intValue(), dataOrder[row].intValue(),
					TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		}else{
			event = new TableModelEvent(this, row, row,
					TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		}
		
		setOrdered(ordered);
		
		callSubscriptors(event);
		
	}
	
	public void addSymbolValue(SymbolValuePOJO poj){
		data.add(poj);
		
		 setOrdered(ordered);
		
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
		if (ordered){
			aux=(SymbolValuePOJO)data.get(dataOrder[rowIndex].intValue());
		}else{
			aux=(SymbolValuePOJO)data.get(rowIndex);
		}
		
		switch(colIndex){
		case 0:
			aux.setSym((Symbol)aValue);
			break;
		case 1:
			int type = aux.getVal().getType();
			try {
					Value val = ValueFactory.createValueByType((String)aValue, type);
					aux.setVal(val);
				} catch (NumberFormatException e) {
					System.out.println("NumberFormatException : "+e.getMessage());
				} catch (ParseException e) {
					System.out.println("ParseException : "+e.getMessage());
				}
			break;
		case 2:
			aux.setLabel((String)aValue);
			break;
		default:
			break;
		}
		
		TableModelEvent event = null;
		if (ordered){
			event = new TableModelEvent (this, dataOrder[rowIndex].intValue(), dataOrder[rowIndex].intValue(), 
		            colIndex);
		}else{
			event = new TableModelEvent (this, rowIndex, rowIndex, 
		            colIndex);
		}
		
	    callSubscriptors(event);
	    setOrdered(ordered);
	}
	
	private void callSubscriptors (TableModelEvent evento)
    {
        int i;
        
        for (i=0; i<listeners.size(); i++)
            ((TableModelListener)listeners.get(i)).tableChanged(evento);
    }
	
	public void setOrdered(boolean selected) {
		ordered=selected;
		if (selected){
			orderTable();
		}
		TableModelEvent event;
		event = new TableModelEvent(this, 0, data.size()-1,
					TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
		callSubscriptors(event);
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
			
			setOrdered(ordered);
		}
		
	}
	
	private void orderTable() {
		
		TreeSet<Integer> values = new TreeSet<Integer>(new Comparator<Integer>() {

            public int compare(Integer i1, Integer i2) {
                Value v1 = data.get(i1.intValue()).getVal();
                Value v2 = data.get(i2.intValue()).getVal();
            	try {
                    if (v1.isNull())
                        return -1;
                    if (v2.isNull())
                        return 1;
                    if (v1.less(v2).getAsBoolean()) {
                        return -1;
                    } else if (v2.less(v1).getAsBoolean()) {
                        return 1;
                    }
                } catch (IncompatibleTypesException e) {
                    throw new RuntimeException(e);
                }

                return -1;
            }
        });
		
		for (int i = 0; i < data.size(); i++) {
            values.add(i);
        }
		dataOrder = new Integer[values.size()];
        dataOrder = values.toArray(new Integer[0]);
        
	}

	private ArrayList<SymbolValuePOJO> data = new ArrayList<SymbolValuePOJO>();
	private Integer [] dataOrder = new Integer[0];
	private LinkedList<TableModelListener> listeners = new LinkedList<TableModelListener>();
	private boolean ordered=false;
	
	
	
}
