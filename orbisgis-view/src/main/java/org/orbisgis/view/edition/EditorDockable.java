
package org.orbisgis.view.edition;

import org.orbisgis.view.docking.DockingPanel;

/**
 * This particular panel is an editor.
 * It has methods related to editable management. 
 */
public interface EditorDockable extends DockingPanel {
        
        /**
         * 
         * @param editableElement
         * @return 
         */
        boolean match(EditableElement editableElement);
        /**
         * Return the currently open editable
         * @return An instance of EditableElement or null
         */
        EditableElement getEditableElement();
        
        /**
         * Load the specified editable element
         * @param editableElement
         * @return false if the editable has not been successfully loaded
         */
        void setEditableElement(EditableElement editableElement);
}
