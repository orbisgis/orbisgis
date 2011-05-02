package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.orbisgis.core.renderer.classification.Range;

/**
 * The table range TableModel
 * @author sennj
 */
class TableModel extends AbstractTableModel {

    private final List<RangeTab> rangesTab = new ArrayList<RangeTab>();
    private final String[] entetes = {"Color", "ValueMin", "ValueMax","NbElem", "Alias"};

    public TableModel(ChoroplethDatas choroDatas) {
        super();

        Range[] ranges = choroDatas.getRange();
        Color[] colors = choroDatas.getClassesColors();
        String[] aliases = choroDatas.getAliases();

        for (int i = 1; i <= ranges.length; i++) {
                rangesTab.add(new RangeTab(colors[i - 1], ranges[i - 1].getMinRange(), ranges[i - 1].getMaxRange(),ranges[i - 1].getNumberOfItems(), aliases[i-1]));
        }
    }

    /**
     * getRowCount
     * @return the row count
     */
    public int getRowCount() {
        return rangesTab.size();
    }

    /**
     * getColumnCount
     * @return the column count
     */
    public int getColumnCount() {
        return entetes.length;
    }

    /**
     * getColumnName
     * @param columnIndex the index of the selected column
     * @return the name of the selected column
     */
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }

    /**
     * getValueAt
     * @param rowIndex the index of the selected row
     * @param columnIndex the index of the selected column
     * @return the object in the selected row/column
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rangesTab.get(rowIndex).getColor();
            case 1:
                return rangesTab.get(rowIndex).getValueMin();
            case 2:
                return rangesTab.get(rowIndex).getValueMax();
            case 3:
                return rangesTab.get(rowIndex).getNbElem();
            case 4:
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
                return Integer.class;
            case 4:
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
            RangeTab ami = rangesTab.get(rowIndex);

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
                    ami.setNbElem((Integer) aValue);
                    break;
                case 4:
                    ami.setAlias((String) aValue);
                    break;
            }
        }
    }

    public void addRanges(RangeTab range) {
        rangesTab.add(range);
        fireTableRowsInserted(rangesTab.size() - 1, rangesTab.size() - 1);
    }

    public void removeRanges(int rowIndex) {
        rangesTab.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

     /**
     * refreshData
     * Refresh the table range elements
     * @param choroDatas
     */
    public void refreshData(ChoroplethDatas choroDatas) {
        Range[] ranges = choroDatas.getRange();
        Color[] colors = choroDatas.getClassesColors();
        String[] aliases = choroDatas.getAliases();

        rangesTab.removeAll(rangesTab);

        for (int i = 1; i <= ranges.length; i++) {
             rangesTab.add( new RangeTab(colors[i - 1], ranges[i - 1].getMinRange(), ranges[i - 1].getMaxRange(),ranges[i - 1].getNumberOfItems(), aliases[i-1]));
        }
    }
}
