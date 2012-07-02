package org.orbisgis.view.toc.actions.cui.choropleth.listener;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.orbisgis.view.toc.actions.cui.freqChart.FreqChart;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;

/**
 * Table listener
 * @author sennj
 */
public class TableListener extends AbstractAction implements
        TableModelListener, PropertyChangeListener, Runnable {

    /** The range table JTable */
    private JTable table;
    /** The choropleth range table panel */
    private FreqChart freqChart;
    /** The frequence chart data model */
    private FreqChartDataModel freqChartDataModel;
    /** The row of the table */
    private int row;
    /** The column of the table */
    private int column;
    /** The old value that is stored */
    private Object oldValue;
    /** The new value that will be stored */
    private Object newValue;

    /**
     * TableListener Creator Create a TableCellListener.
     * @param table The table to be monitored for data changes
     * @param freqChartDataModel The frequence chart data model
     * @param freqChart The frequency chart panel
     */
    public TableListener(JTable table,
            FreqChartDataModel freqChartDataModel, FreqChart freqChart) {
        this.table = table;
        this.freqChartDataModel = freqChartDataModel;
        this.freqChart = freqChart;
        this.table.addPropertyChangeListener(this);
    }

    /**
     * TableListener Creator Create a TableCellListener with a copy of all the
     * data relevant to the change of data for a given cell.
     * @param table The table to be monitored for data changes
     * @param row the row of the changed cell
     * @param column The column of the changed cell
     * @param oldValue The old data of the changed cell
     * @param newValue The new data of the changed cell
     */
    private TableListener(JTable table, int row, int column, Object oldValue,
            Object newValue) {
        this.table = table;
        this.row = row;
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Get the column that was last edited
     * @return the column that was edited
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get the new value in the cell
     * @return the new value in the cell
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * Get the old value of the cell
     * @return the old value of the cell
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * Get the row that was last edited
     * @return the row that was edited
     */
    public int getRow() {
        return row;
    }

    /**
     * Get the table of the cell that was changed
     * @return the table of the cell that was changed
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Implement the PropertyChangeListener interface
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        // A cell has started/stopped editing
        if ("tableCellEditor".equals(e.getPropertyName())) {
            if (table.isEditing()) {
                processEditingStarted();
            } else {
                processEditingStopped();
            }
        }
    }

    /**
     * Save information of the cell about to be edited
     */
    private void processEditingStarted() {
        // The invokeLater is necessary because the editing row and editing
        // column of the table have not been set when the "tableCellEditor"
        // PropertyChangeEvent is fired.
        // This results in the "run" method being invoked

        SwingUtilities.invokeLater(this);
    }

    @Override
    public void run() {
        row = table.convertRowIndexToModel(table.getEditingRow());
        column = table.convertColumnIndexToModel(table.getEditingColumn());
        oldValue = table.getModel().getValueAt(row, column);
        newValue = null;
    }

    /**
     * Update the Cell history when necessary
     */
    private void processEditingStopped() {
        newValue = table.getModel().getValueAt(row, column);

        // The data has changed, invoke the supplied Action

        if (!newValue.equals(oldValue)) {
            // Make a copy of the data in case another cell starts editing
            // while processing this change

            TableListener tcl = new TableListener(getTable(), getRow(),
                    getColumn(), getOldValue(), getNewValue());

            ActionEvent event = new ActionEvent(tcl,
                    ActionEvent.ACTION_PERFORMED, "");
            this.actionPerformed(event);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (column == 1) {
            refreshAlias(freqChartDataModel, row, newValue);
        }
    }

    /**
     * Refresh the alias
     * @param freqChartDataModel The frequence chart data model
     * @param row The selected row
     * @param newValue The new value to apply
     */
    public void refreshAlias(FreqChartDataModel freqChartDataModel, int row,
            Object newValue) {
        freqChartDataModel.setLabel(row, (String) newValue);
        table.repaint();
        freqChart.repaint();
    }
}
