
package org.orbisgis.view.docking.internals;

import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.action.CAction;
import org.orbisgis.view.docking.DockingPanel;

/**
 * A custom cdockable that contains a reference to the dockingPanel instance
 */
public class CustomMultipleCDockable extends DefaultMultipleCDockable {
    private DockingPanel dockingPanel;

    public CustomMultipleCDockable(DockingPanel dockingPanel, MultipleCDockableFactory<?, ?> factory, CAction... actions) {
        super(factory, actions);
        this.dockingPanel = dockingPanel;
    }
    /**
     * 
     * @return a reference to the dockingPanel instance
     */
    public DockingPanel getDockingPanel() {
        return dockingPanel;
    }
    
}
