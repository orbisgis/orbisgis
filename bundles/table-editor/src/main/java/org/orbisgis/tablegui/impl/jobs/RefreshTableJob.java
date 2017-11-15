package org.orbisgis.tablegui.impl.jobs;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tablegui.impl.DataSourceRowSorter;
import org.orbisgis.tablegui.impl.DataSourceTableModel;
import org.orbisgis.tablegui.impl.TableEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Nicolas Fortin
 */
public class RefreshTableJob extends SwingWorkerPM<Boolean, Boolean> {
    protected final static I18n I18N = I18nFactory.getI18n(RefreshTableJob.class);
    private static final Logger LOGGER = LoggerFactory.getLogger("gui." + RefreshTableJob.class);
    private DataSourceTableModel model;
    private JTable tableComp;
    private TableEditableElement table;
    private List<TableModelEvent> evts = new ArrayList<>();
    private TableEditEvent event;

    public RefreshTableJob(DataSourceTableModel model, TableEditableElement table, TableEditEvent event,
                           JTable tableComp) {
        this.model = model;
        this.table = table;
        this.event = event;
        this.tableComp = tableComp;
        setTaskName(I18N.tr("Refresh table content"));
    }

    @Override
    protected void done() {
        model.setLastFetchRowCountTime(0);
        // Swing Thread
        // Send columns delete/insert/update events
        Rectangle rect = tableComp.getVisibleRect();
        int firstVisibleRow = tableComp.rowAtPoint(rect.getLocation());
        int lastVisibleRow = tableComp.rowAtPoint(new Point(rect.x, rect.y + rect.height - 1));
        if(firstVisibleRow < lastVisibleRow && firstVisibleRow >= 0 && lastVisibleRow <= tableComp.getRowCount()) {
            IntegerUnion rowsToClean = new IntegerUnion();
            for(int viewRow = firstVisibleRow; viewRow <= lastVisibleRow; viewRow++) {
                rowsToClean.add(tableComp.convertRowIndexToModel(viewRow) + 1);
            }
            try {
                table.getRowSet().refreshRows(rowsToClean);
                // Update rendered rows
                Iterator<Integer> intervals = rowsToClean.getValueRanges().iterator();
                while(intervals.hasNext()) {
                    int start = intervals.next();
                    int end = intervals.next();
                    model.fireTableRowsUpdated(start - 1, end - 1);
                }
            } catch (SQLException | EditableElementException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }

        if(evts.isEmpty()) {
            // Refresh shown data
            model.fireTableDataChanged();
        } else {
            for (TableModelEvent evt : evts) {
                model.fireTableChanged(evt);
            }
        }
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        if(event.getColumn() == TableModelEvent.ALL_COLUMNS || event.getFirstRowPK() == null || event.getLastRowPK() == null) {
            List<String> columnTypes = new ArrayList<>();
            List<String> columnNames = new ArrayList<>();
            try {
                try {
                    if(!table.isOpen()) {
                        table.open(getProgressMonitor());
                    }
                    ResultSetMetaData meta = table.getRowSet().getMetaData();
                    for (int col = 1; col < meta.getColumnCount(); col++) {
                        columnNames.add(meta.getColumnName(col));
                        columnTypes.add(meta.getColumnTypeName(col));
                    }
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
                // The row count may have changed, reset the rowset
                table.getRowSet().execute();
                try {
                    ResultSetMetaData meta = table.getRowSet().getMetaData();
                    for (int col = 1; col < meta.getColumnCount(); col++) {
                        if (col <= columnNames.size()) {
                            if (!columnNames.get(col - 1).equals(meta.getColumnName(col)) || !columnTypes.get(col - 1).equals(meta.getColumnTypeName(col))) {
                                evts.add(new TableModelEvent(model, TableModelEvent.HEADER_ROW, TableModelEvent.HEADER_ROW, col - 1, TableModelEvent.UPDATE));
                            }
                            //columnTypes.add(meta.getColumnTypeName(col + offset));
                        } else {
                            //New column
                            evts.add(new TableModelEvent(model, TableModelEvent.HEADER_ROW, TableModelEvent.HEADER_ROW, col - 1, TableModelEvent.INSERT));
                        }
                    }
                    // Deleted columns
                    if (meta.getColumnCount() < columnNames.size()) {
                        for (int insertId = meta.getColumnCount(); insertId <= columnNames.size(); insertId++) {
                            evts.add(new TableModelEvent(model, TableModelEvent.HEADER_ROW, TableModelEvent.HEADER_ROW, meta.getColumnCount() - 1, TableModelEvent.DELETE));
                        }
                    }
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
            } catch (EditableElementException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        } else {
            // Simple row event
            IntegerUnion updatedRows = new IntegerUnion(table.getRowSet().getRowNumberFromRowPk(new LongUnion(event.getFirstRowPK(), event.getLastRowPK())));
            Iterator<Integer> intervals = updatedRows.getValueRanges().iterator();
            while (intervals.hasNext()) {
                int firstRow = intervals.next();
                int lastRow = intervals.next();
                evts.add(new TableModelEvent(model, firstRow - 1, lastRow - 1, event.getColumn(), event.getType() ));
            }
            // Refresh rowset cache
            table.getRowSet().refreshRows(new TreeSet<>(updatedRows));
        }
        return true;
    }
}
