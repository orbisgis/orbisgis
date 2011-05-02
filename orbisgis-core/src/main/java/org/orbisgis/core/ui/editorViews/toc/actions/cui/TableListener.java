package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.parameter.ParameterException;


/*
 * This class listens for changes made to the data in the table via the
 * TableCellEditor. When editing is started, the value of the cell is saved
 * When editing is stopped the new value is saved. When the oold and new
 * values are different, then the provided Action is invoked.
 */
public class TableListener extends AbstractAction implements TableModelListener, PropertyChangeListener, Runnable {

    private JTable table;
    private ChoroplethDatas choroDatas;
    private int row;
    private int column;
    private Object oldValue;
    private Object newValue;

    /**
     * TableListener Creator
     * Create a TableCellListener.
     * @param table   the table to be monitored for data changes
     * @param action  the Action to invoke when cell data is changed
     */
    public TableListener(JTable table, ChoroplethDatas choroDatas) {
        this.table = table;
        this.choroDatas = choroDatas;
        this.table.addPropertyChangeListener(this);
    }

    /**
     * TableListener Creator
     * Create a TableCellListener with a copy of all the data relevant to
     * the change of data for a given cell.
     * @param row  the row of the changed cell
     * @param column  the column of the changed cell
     * @param oldValue  the old data of the changed cell
     * @param newValue  the new data of the changed cell
     */
    private TableListener(JTable table, int row, int column, Object oldValue, Object newValue) {
        this.table = table;
        this.row = row;
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * getColumn
     * Get the column that was last edited
     * @return the column that was edited
     */
    public int getColumn() {
        return column;
    }

    /**
     * getNewValue
     * Get the new value in the cell
     * @return the new value in the cell
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * getOldValue
     * Get the old value of the cell
     * @return the old value of the cell
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * getRow
     * Get the row that was last edited
     * @return the row that was edited
     */
    public int getRow() {
        return row;
    }

    /**
     * getTable
     * Get the table of the cell that was changed
     * @return the table of the cell that was changed
     */
    public JTable getTable() {
        return table;
    }
//
//  Implement the PropertyChangeListener interface
//

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        //  A cell has started/stopped editing
        if ("tableCellEditor".equals(e.getPropertyName())) {
            if (table.isEditing()) {
                processEditingStarted();
            } else {
                processEditingStopped();
            }
        }
    }

    /*
     *  Save information of the cell about to be edited
     */
    private void processEditingStarted() {
        //  The invokeLater is necessary because the editing row and editing
        //  column of the table have not been set when the "tableCellEditor"
        //  PropertyChangeEvent is fired.
        //  This results in the "run" method being invoked

        choroDatas.setStatisticMethod(ChoroplethDatas.StatisticMethod.MANUAL, false);
        SwingUtilities.invokeLater(this);
    }


    @Override
    public void run() {
        row = table.convertRowIndexToModel(table.getEditingRow());
        column = table.convertColumnIndexToModel(table.getEditingColumn());
        oldValue = table.getModel().getValueAt(row, column);
        newValue = null;
    }

    /*
     *	Update the Cell history when necessary
     */
    private void processEditingStopped() {
        newValue = table.getModel().getValueAt(row, column);

        //  The data has changed, invoke the supplied Action

        if (!newValue.equals(oldValue)) {
            //  Make a copy of the data in case another cell starts editing
            //  while processing this change

            TableListener tcl = new TableListener(
                    getTable(), getRow(), getColumn(), getOldValue(), getNewValue());

            ActionEvent event = new ActionEvent(tcl, ActionEvent.ACTION_PERFORMED, "");
            this.actionPerformed(event);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (column == 1 && row != 0) {
            table.setValueAt(newValue, row - 1, column + 1);
            refreshRange(choroDatas, row - 1, column, newValue);
        }
        if (column == 2 && row != choroDatas.getRange().length) {
            table.setValueAt(newValue, row + 1, column - 1);
            refreshRange(choroDatas, row, column, newValue);
        }
        if (column == 4) {
            refreshAlias(choroDatas, row, newValue);
        }
    }

    /**
     * refreshRange
     * Refresh the range
     * @param choroDatas the datas to draw
     * @param row the selected row
     * @param column the selected column
     * @param newValue the new value to apply
     */
    public void refreshRange(ChoroplethDatas choroDatas, int row, int column, Object newValue) {
        Range[] ranges = choroDatas.getRange();
        ranges[row].setMaxRange(Double.valueOf((Double) newValue));

        if (!(row == choroDatas.getRange().length && column == 2)) {
            ranges[row + 1].setMinRange(Double.valueOf((Double) newValue));
        }

        double[] choroData;
        int nbElemRangeBefore = 0;
        int nbElemRange = 0;

        try {
            choroData = choroDatas.getSortedData();
            for (int i = 1; i <= choroData.length; i++) {
                if (choroData[i - 1] >= ranges[row].getMinRange()
                        && choroData[i - 1] < ranges[row].getMaxRange()) {
                    nbElemRangeBefore++;
                }
                if (choroData[i - 1] >= ranges[row + 1].getMinRange()
                        && choroData[i - 1] < ranges[row + 1].getMaxRange()) {
                    nbElemRange++;
                }
            }
        } catch (ParameterException ex) {
            Logger.getLogger(TableListener.class.getName()).log(Level.SEVERE, null, ex);
        }

        ranges[row].setNumberOfItems(nbElemRangeBefore);
        ranges[row + 1].setNumberOfItems(nbElemRange);

        choroDatas.setRange(ranges);
        choroDatas.calculateColors();
        table.repaint();
    }

     /**
     * refreshAlias
     * Refresh the alias
     * @param choroDatas the datas to draw
     * @param row the selected row
     * @param newValue the new value to apply
     */
    public void refreshAlias(ChoroplethDatas choroDatas, int row, Object newValue)
    {
        choroDatas.setAlias((String)newValue, row);
        table.repaint();
    }
}
