package org.orbisgis.view.edition;

/**
 * SingleEditorFactory defines only
 */

public interface SingleEditorFactory extends EditorFactory {
   /**
     * If the editor factory manage panels that cannot be closed.
     * This method is called once, just before the layout loading
     * @return Array of DockingPanel instance
     */
    EditorDockable[] getSinglePanels();
    
            
}
