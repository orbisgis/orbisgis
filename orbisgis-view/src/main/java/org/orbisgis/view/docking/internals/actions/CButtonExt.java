package org.orbisgis.view.docking.internals.actions;

import bibliothek.gui.dock.common.action.CButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Nicolas Fortin
 */
public class CButtonExt extends CButton implements CActionHolder {
    private Action action;

    public CButtonExt(Action action) {
        this.action = action;
        this.action = action;
        // Read properties from the action
        onActionPropertyChange(new PropertyChangeEvent(action,null,null,null));
        // Listen to action property changes
        action.addPropertyChangeListener(
                EventHandler.create(PropertyChangeListener.class, this, "onActionPropertyChange", ""));
    }

    @Override
    protected void action() {
        super.action();
        action.actionPerformed(new ActionEvent( this, ActionEvent.ACTION_PERFORMED, null ));
    }

    /**
     * Used by PropertyChangeListener, update CAction properties
     * @param propertyChangeEvent Information on update event.
     */
    public void onActionPropertyChange(PropertyChangeEvent propertyChangeEvent) {
        CommonFunctions.onActionPropertyChangeDecorateable(this, action, propertyChangeEvent);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
