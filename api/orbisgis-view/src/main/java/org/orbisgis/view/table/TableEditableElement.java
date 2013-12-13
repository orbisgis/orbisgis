package org.orbisgis.view.table;

import org.orbisgis.view.edition.EditableSource;

import java.util.Set;

/**
 * @author Nicolas Fortin
 */
public interface TableEditableElement extends EditableSource {

    /**
     * @return the selected rows in the table
     */
    public Set<Integer> getSelection();

    /**
     * Set the selected geometries in the table
     * @param selection Row's id
     */
    public void setSelection(Set<Integer> selection);
}
