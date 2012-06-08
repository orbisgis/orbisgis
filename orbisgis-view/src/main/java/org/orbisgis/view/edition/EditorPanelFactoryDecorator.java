
package org.orbisgis.view.edition;

import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelFactory;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 * MultipleEditorFactory must define EditorPanels instead of DockingPanel
 * then this decorator 
 * make the link between a MultipleEditorFactory and a DcokingPanelFactory
 */

public class EditorPanelFactoryDecorator implements DockingPanelFactory {
        private MultipleEditorFactory multipleEditorFactory;

        public EditorPanelFactoryDecorator(MultipleEditorFactory multipleEditorFactory) {
                this.multipleEditorFactory = multipleEditorFactory;
        }

        public DockingPanelLayout makeEmptyLayout() {
                return multipleEditorFactory.makeEmptyLayout();
        }

        public boolean match(DockingPanelLayout layout) {
                return multipleEditorFactory.match(layout);
        }

        public DockingPanel create(DockingPanelLayout layout) {
                return multipleEditorFactory.create(layout);
        }

        public DockingPanelLayout getLayout(DockingPanel panel) {
                if(!(panel instanceof EditorDockable)) {
                        throw new IllegalArgumentException("Editors factory accepts only EditorDockable layout");
                }
                return multipleEditorFactory.getLayout((EditorDockable)panel);
        }
        
}
