package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;

/**
 * The table range TableModel
 * @author sennj
 */
class TableModel extends AbstractTableModel {

    private final List<RangeTab> rangesTab = new ArrayList<RangeTab>();
    private final String[] entetes = {"Color", "Alias"};

    /**
     * TableModel constructor
     * @param freqChartDataModel the data to model draw
     */
    public TableModel(FreqChartDataModel freqChartDataModel) {
        super();

        List<double[]> seuil = freqChartDataModel.getSeuilList();
        List<Color> colors = freqChartDataModel.getColor();
        List<String> aliases = freqChartDataModel.getLabel();


        for (int i = 1; i <= seuil.size(); i++) {
            rangesTab.add(new RangeTab(colors.get(i - 1), aliases.get(i - 1)));
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
     *
     * @return the column count
     */
    public int getColumnCount() {
        return entetes.length;
    }

    /**
     * getColumnName
     *
     * @param columnIndex
     *            the index of the selected column
     * @return the name of the selected column
     */
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }

    /**
     * getValueAt
     *
     * @param rowIndex
     *            the index of the selected row
     * @param columnIndex
     *            the index of the selected column
     * @return the object in the selected row/column
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rangesTab.get(rowIndex).getColor();
            case 1:
                return rangesTab.get(rowIndex).getAlias();
            default:
                return null; // Ne devrait jamais arriver
            }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Color.class;
            case 1:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        }
        return true;
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
                    ami.setAlias((String) aValue);
                    break;
            }
        }
    }

    /**
     * Add a range to tab
     * @param range
     */
    public void addRanges(RangeTab range) {
        rangesTab.add(range);
        fireTableRowsInserted(rangesTab.size() - 1, rangesTab.size() - 1);
    }

    /**
     * Remove a range to tab
     * @param rowIndex
     */
    public void removeRanges(int rowIndex) {
        rangesTab.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    /**
     * refreshData Refresh the table range elements
     *
     * @param freqChartDataModel the data to model draw
     */
    public void refreshDatas(FreqChartDataModel freqChartDataModel) {
        List<double[]> seuil = freqChartDataModel.getSeuilList();
        List<Color> colors = freqChartDataModel.getColor();
        List<String> aliases = freqChartDataModel.getLabel();

        rangesTab.removeAll(rangesTab);

        for (int i = 1; i <= seuil.size(); i++) {
            rangesTab.add(new RangeTab(colors.get(i - 1), aliases.get(i - 1)));
        }
    }
}
