package org.orbisgis.core.api;

import javax.sql.RowSet;
import javax.swing.event.UndoableEditListener;
import java.sql.SQLException;

/**
 * This kind of RowSet hold an history of update commands. Undo and redo methods are available.
 * @author Nicolas Fortin
 */
public interface ReversibleRowSet extends RowSet {

    /**
     * @param listener Undoable edit listener
     */
    void addUndoableEditListener(UndoableEditListener listener);
    /**
     * @param listener Undoable edit listener
     */
    void removeUndoableEditListener(UndoableEditListener listener);

    /**
     * @return Number of rows inside the table
     */
    long getRowCount() throws SQLException;

}
