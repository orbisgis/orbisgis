package org.orbisgis.editors.table;

import org.gdms.data.DataSource;
import org.orbisgis.edition.EditableElement;

/**
 * Interface to be implemented by those EditableElements that need to be edited
 * by the table editor
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public interface TableEditableElement extends EditableElement {

	/**
	 * Get the object that manages selection
	 * 
	 * @return
	 */
	Selection getSelection();

	/**
	 * Get the data to populate the table
	 * 
	 * @return
	 */
	DataSource getDataSource();

	/**
	 * Return true if the source can be edited
	 * 
	 * @return
	 */
	boolean isEditable();
}
