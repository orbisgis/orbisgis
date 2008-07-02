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
package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.text.ParseException;
import java.util.LinkedList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.orbisgis.renderer.legend.Interval;
import org.orbisgis.renderer.symbol.Symbol;

public class SymbolIntervalTableModel implements TableModel {

	
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
                return "Interval";
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
		SymbolIntervalPOJO aux;
		
		aux=(SymbolIntervalPOJO)data.get(rowIndex);
		
		switch(colIndex){
		case -1:
			return aux;
		case 0:
			return aux.getSym();
		case 1:
			return aux.getVal().getIntervalString();
		case 2:
			return aux.getLabel();
		case 3:
			return aux.getVal();
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
	
	public void addSymbolValue(SymbolIntervalPOJO poj){
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
		SymbolIntervalPOJO aux;
		aux=(SymbolIntervalPOJO)data.get(rowIndex);
		
		switch(colIndex){
		case 0:
			aux.setSym((Symbol)aValue);
			break;
		case 1:
			int type1 = aux.getVal().getMinValue().getType();
			int type2 = aux.getVal().getMaxValue().getType();
			try {
					String [] values = ((String)aValue).split("-");
					Value val1 = ValueFactory.createValueByType(values[0].trim(), type1);
					Value val2 = ValueFactory.createValueByType(values[1].trim(), type2);
					Interval inter = new Interval(val1, true, val2, false);
					
					aux.setVal(inter);
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


	private LinkedList<SymbolIntervalPOJO> data = new LinkedList<SymbolIntervalPOJO>();
	private LinkedList<TableModelListener> listeners = new LinkedList<TableModelListener>();
	
	
}
