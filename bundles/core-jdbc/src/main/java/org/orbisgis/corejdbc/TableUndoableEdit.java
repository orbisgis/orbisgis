package org.orbisgis.corejdbc;

import java.sql.SQLException;

/**
 * A table edit that can be undone.
 * @author Nicolas Fortin
 */
public interface TableUndoableEdit {
    /**
     * Run SQL commands in order to undo changes.
     * @throws java.sql.SQLException If an error occurred while undoing
     */
    public void undo() throws SQLException;

    /**
     * @return true if SQL commands are available in order to undo the changes.
     */
    public boolean canUndo();

    /**
     * Re-applies the SQL commands in order to redo changes.
     *
     * @throws java.sql.SQLException If an error occurred while redoing
     */
    public void redo() throws SQLException;

    /**
     * @return true if SQL commands are available in order to redo changes.
     */
    public boolean canRedo();

    /**
     * Informs the edit that it should no longer be used. Free stored SQL commands. Undo/Redo are no longer applicable.
     */
    public void die();

    /**
     * @return true if an undo task should stop to this undoable edit.
     */
    public boolean isSignificant();

    /**
     * @return Edit identifier for this category of edit.
     */
    public String getEditIdentifier();

    /**
     * @return Localized message associated with this edit.
     */
    public String getPresentationName();

    /**
     * @return Localized message associated with this edit undo task.
     */
    public String getUndoPresentationName();

    /**
     * @return Localized message associated with this edit redo task.
     */
    public String getRedoPresentationName();
}
