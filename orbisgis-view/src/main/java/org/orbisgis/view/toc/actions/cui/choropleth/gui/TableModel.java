package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The table range TableModel
 * @author sennj
 */
class TableModel extends AbstractTableModel {

    /** I18n */
    private final static I18n I18N = I18nFactory.getI18n(TableModel.class);

    /** The list of the range table elements */
    private final List<RangeTab> rangesTab = new ArrayList<RangeTab>();
    /** The header of the table */
    private final String[] header = {I18N.tr("Color"), I18N.tr("Alias")};

    /**
     * TableModel constructor
     * @param freqChartDataModel The frequence chart data model
     */
    public TableModel(FreqChartDataModel freqChartDataModel) {
        super();

        List<List<Double>> threshold = freqChartDataModel.getThresholdList();
        List<Color> colors = freqChartDataModel.getColor();
        List<String> aliases = freqChartDataModel.getLabel();

        for (int i = 1; i <= threshold.size(); i++) {
            rangesTab.add(new RangeTab(colors.get(i - 1), aliases.get(i - 1)));
        }
    }

    /**
     * Get the count of row
     * @return the row count
     */
    @Override
    public int getRowCount() {
        return rangesTab.size();
    }

    /**
     * Get the count of column
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return header.length;
    }

    /**
     * Get column header name
     * @param columnIndex The index of the selected column
     * @return the name of the selected column
     */
    @Override
    public String getColumnName(int columnIndex) {
        return header[columnIndex];
    }

    /**
     * Get the value from the range
     * @param rowIndex The index of the selected row
     * @param columnIndex The index of the selected column
     * @return the object in the selected row/column
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rangesTab.get(rowIndex).getColor();
            case 1:
                return rangesTab.get(rowIndex).getAlias();
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Get the column class type
     * @param columnIndex The index of the selected column
     * @return the class type
     */
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

    /**
     * Test if the cell is editable
     * @param rowIndex The index of the selected row
     * @param columnIndex The index of the selected column
     * @return a boolean that test if the cell is editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        }
        return true;
    }

    /**
     * Set the value of the table element
     * @param aValue The value of the selected column
     * @param rowIndex The index of the selected row
     * @param columnIndex The index of the selected column
     */
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
     * Add an element to table
     * @param range an element of the table
     */
    public void addRanges(RangeTab range) {
        rangesTab.add(range);
        fireTableRowsInserted(rangesTab.size() - 1, rangesTab.size() - 1);
    }

    /**
     * Remove an element to tab
     * @param rowIndex The index of the selected row
     */
    public void removeRanges(int rowIndex) {
        rangesTab.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    /**
     * Refresh the table range elements
     * @param freqChartDataModel The frequence chart data model
     */
    public void refreshDatas(FreqChartDataModel freqChartDataModel) {
        List<List<Double>> threshold = freqChartDataModel.getThresholdList();
        List<Color> colors = freqChartDataModel.getColor();
        List<String> aliases = freqChartDataModel.getLabel();

        rangesTab.removeAll(rangesTab);

        for (int i = 1; i <= threshold.size(); i++) {
            rangesTab.add(new RangeTab(colors.get(i - 1), aliases.get(i - 1)));
        }
    }
}
