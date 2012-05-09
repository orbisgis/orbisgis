
package org.orbisgis.view.docking.internals;


import bibliothek.gui.dock.common.MultipleCDockableFactory;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelFactory;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 *
 */
public class InternalCommonFactory implements MultipleCDockableFactory<CustomMultipleCDockable, DockingPanelLayout> {
    
        private DockingPanelFactory factory;

        public InternalCommonFactory(DockingPanelFactory factory) {
            this.factory = factory;
        }
            
        /* An empty layout is required to read a layout from an XML file or from a byte stream */
        public DockingPanelLayout create(){
                return factory.makeEmptyLayout();
        }

        /* An optional method allowing to reuse 'dockable' when loading a new layout */
        public boolean match( CustomMultipleCDockable dockable, DockingPanelLayout layout ){
                return factory.match(layout);
        }

        /* Called when applying a stored layout */
        public CustomMultipleCDockable read( DockingPanelLayout layout ){
                DockingPanel panel = factory.create(layout);
                CustomMultipleCDockable dockable = OrbisGISView.createMultiple(panel, this);                
                return dockable;
        }

        /* Called when storing the current layout */
        public DockingPanelLayout write( CustomMultipleCDockable dockable ){
                return factory.getLayout(dockable.getDockingPanel());
        }
}
