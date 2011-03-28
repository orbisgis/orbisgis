/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.orbisgis.core.renderer.classification.Range;

/**
 *
 * @author sennj
 */
class JSE_TableModel extends AbstractTableModel {

    JSE_ChoroplethDatas ChoroDatas;
    private final List<JSE_RangeTab> rangesTab = new ArrayList<JSE_RangeTab>();
    private final String[] entetes = {"Color", "ValueMin", "ValueMax", "Alias"};

    public JSE_TableModel(JSE_ChoroplethDatas ChoroDatas) {
        super();

        this.ChoroDatas = ChoroDatas;

        Range[] ranges = ChoroDatas.getRange();
        Color[] colors = ChoroDatas.getClassesColors();

        for (int i = 1; i <= ranges.length; i++) {
            rangesTab.add(new JSE_RangeTab(colors[i - 1], ranges[i - 1].getMinRange(), ranges[i - 1].getMaxRange(), String.valueOf(i)));
        }
    }

    public int getRowCount() {
        return rangesTab.size();
    }

    public int getColumnCount() {
        return entetes.length;
    }

    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rangesTab.get(rowIndex).getColor();
            case 1:
                return rangesTab.get(rowIndex).getValueMin();
            case 2:
                return rangesTab.get(rowIndex).getValueMax();
            case 3:
                return rangesTab.get(rowIndex).getAlias();
            default:
                return null; //Ne devrait jamais arriver
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Color.class;
            case 1:
                return Double.class;
            case 2:
                return Double.class;
            case 3:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; //Toutes les cellules éditables
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue != null) {
            JSE_RangeTab ami = rangesTab.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    ami.setColor((Color) aValue);
                    break;
                case 1:
                    ami.setValueMin((Double) aValue);
                    break;
                case 2:
                    ami.setValueMax((Double) aValue);
                    break;
                case 3:
                    ami.setAlias((String) aValue);
                    break;
            }
        }
    }

    public void addRanges(JSE_RangeTab range) {
        rangesTab.add(range);
        fireTableRowsInserted(rangesTab.size() - 1, rangesTab.size() - 1);
    }

    public void removeRanges(int rowIndex) {
        rangesTab.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void refreshData(JSE_ChoroplethDatas ChoroDatas) {
        Range[] ranges = ChoroDatas.getRange();
        Color[] colors = ChoroDatas.getClassesColors();

        rangesTab.removeAll(rangesTab);
        
        for (int i = 1; i <= ranges.length; i++) {
            rangesTab.add( new JSE_RangeTab(colors[i - 1], ranges[i - 1].getMinRange(), ranges[i - 1].getMaxRange(), String.valueOf(i)));
        }


    }
}
