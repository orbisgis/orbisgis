package org.orbisgis.tablegui.api;

import org.orbisgis.editorjdbc.EditableSource;
import org.orbisgis.sif.edition.EditableElementException;

import java.util.Set;
import java.util.SortedSet;

/**
 * Transfer this class to {@link org.orbisgis.sif.edition.EditorManager} in order to open a new table editor
 * @author Nicolas Fortin
 */
public interface TableEditableElement extends EditableSource {
    // Properties names
    public static final String PROP_SELECTION = "selection";


    /**
     * @return Primary keys of the selected rows in the table
     */
    public SortedSet<Long> getSelection();

    /**
     * Set the selected rows in the table using primary key values.
     * @param selection Row's id
     */
    public void setSelection(Set<Long> selection);

    /**
     * @return Row number [1-n] of the selected rows
     */
    public SortedSet<Integer> getSelectionTableRow() throws EditableElementException;

    /**
     * Update selection using row number.
     * @param selection Row number [1-n] of the selected rows
     */
    public void setSelectionTableRow(SortedSet<Integer> selection) throws EditableElementException;
}
