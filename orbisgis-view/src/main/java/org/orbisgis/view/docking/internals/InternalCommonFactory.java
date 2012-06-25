
package org.orbisgis.view.docking.internals;


import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelFactory;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 *
 */
public class InternalCommonFactory implements MultipleCDockableFactory<CustomMultipleCDockable, DockingPanelLayout> {
    
        private DockingPanelFactory factory;
        private CControl ccontrol;

        public InternalCommonFactory(DockingPanelFactory factory, CControl ccontrol) {
                this.factory = factory;
                this.ccontrol = ccontrol;
        }

            
        /* An empty layout is required to read a layout from an XML file or from a byte stream */
        @Override
        public DockingPanelLayout create(){
                return factory.makeEmptyLayout();
        }

        /* An optional method allowing to reuse 'dockable' when loading a new layout */
        @Override
        public boolean match( CustomMultipleCDockable dockable, DockingPanelLayout layout ){
                return factory.match(layout);
        }

        /* Called when applying a stored layout */
        @Override
        public CustomMultipleCDockable read( DockingPanelLayout layout ){
                DockingPanel panel = factory.create(layout);
                CustomMultipleCDockable dockable = OrbisGISView.createMultiple(panel, this, ccontrol);                
                return dockable;
        }

        /* Called when storing the current layout */
        @Override
        public DockingPanelLayout write( CustomMultipleCDockable dockable ){
                return factory.getLayout(dockable.getDockingPanel());
        }
}
