package org.orbisgis.view.edition;

import org.orbisgis.view.docking.DockingPanelLayout;

/**
 * This factory can create an unlimited number of editors.
 * The panels opened by this editor should by retrieved at the next
 * application startup thanks to lightweight custom panel layout
 */

public interface MultipleEditorFactory extends EditorFactory {

    
    /**
     * Return a layout corresponding to this editable element
     * @param editable editable element
     * @return Instance of DockingPanelLayout or null
     */
    public DockingPanelLayout makeEditableLayout(EditableElement editable);    
      
    
    // Serialisation
    
    /**
     * Create an empty layout, will be used to apply XML file or a byte stream
     * @return 
     */
    public DockingPanelLayout makeEmptyLayout();
    
    /**
     * 
     * @param layout
     * @return True if the layout corresponding to the layout of this factory
     */
    public boolean match(DockingPanelLayout layout);
    
    /**
     * Return a new DockingPanel for this serialised panel
     * @param layout
     * @return 
     */
    public EditorDockable create(DockingPanelLayout layout);
    
    /**
     * Retrieves the persistent information of this panel
     * @note The provided panel was created by this factory
     * @param panel
     * @return 
     */
    public DockingPanelLayout getLayout(EditorDockable panel);        
}
