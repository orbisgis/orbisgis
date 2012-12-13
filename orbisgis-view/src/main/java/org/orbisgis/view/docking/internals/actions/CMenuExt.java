package org.orbisgis.view.docking.internals.actions;

import bibliothek.gui.dock.common.action.CMenu;
import org.orbisgis.view.components.actions.ActionTools;

import javax.swing.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Nicolas Fortin
 */
public class CMenuExt extends CMenu {
    private Action action;
    /**
     * Create the menu using Action properties.
     * @param action Swing Action
     */
    public CMenuExt(Action action) {
        this.action = action;
        // Read properties from the action
        onActionPropertyChange(new PropertyChangeEvent(action,null,null,null));
        // Listen to action property changes
        action.addPropertyChangeListener(
                EventHandler.create(PropertyChangeListener.class, this, "onActionPropertyChange", ""));
    }
    /**
     * Used by PropertyChangeListener, update CMenuExt properties
     * @param propertyChangeEvent
     */
    public void onActionPropertyChange(PropertyChangeEvent propertyChangeEvent) {
        CommonFunctions.onActionPropertyChangeDecorateable(this,action,propertyChangeEvent);
    }
}
