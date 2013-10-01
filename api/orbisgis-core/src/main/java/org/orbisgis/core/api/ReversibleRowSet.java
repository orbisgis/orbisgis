package org.orbisgis.core.api;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * This kind of RowSet hold an history of update commands. Undo and redo methods are available.
 * @author Nicolas Fortin
 */
public interface ReversibleRowSet extends RowSet {
    /**
     * Redoes the last undone edition action. Raise a RowSetEvent.
     * @throws SQLException if there is no action to redo ({@link #canRedo()} returns false)
     */
    void redo() throws SQLException;

    /**
     * Undoes the last edition action. Raise a RowSetEvent.
     * @throws SQLException if there is no action to undo ({@link #canUndo()} returns false)
     */
    void undo() throws SQLException;

    /**
     * @return true if there is an edition action to redo
     */
    boolean canRedo();

    /**
     * @return true if there is an edition action to undo
     */
    boolean canUndo();

    /**
     * Clear the undo/redo history
     */
    void clearHistory();
}
