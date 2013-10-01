package org.orbisgis.view.geocatalog.actions;

import org.apache.log4j.Logger;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.view.components.actions.DefaultAction;
import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Action shown only if the SourceManager contain something else than the system sources.
 * @author Nicolas Fortin
 */
public class ActionOnNonEmptySourceList extends DefaultAction {
    private static Logger LOG = Logger.getLogger(ActionOnNonEmptySourceList.class);
    private ListModel<ContainerItemProperties> srcModel;

    /**
     * @param actionId Action identifier, should be unique for ActionCommands
     * @param actionLabel I18N label short label
     * @param actionToolTip I18N tool tip text
     * @param icon Icon
     * @param actionListener Fire the event to this listener
     */
    public ActionOnNonEmptySourceList(String actionId, String actionLabel,String actionToolTip, Icon icon,ActionListener actionListener, ListModel<ContainerItemProperties> model) {
        super(actionId, actionLabel, actionToolTip, icon, actionListener, null);
        this.srcModel = model;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && srcModel.getSize()!=0;
    }
}
