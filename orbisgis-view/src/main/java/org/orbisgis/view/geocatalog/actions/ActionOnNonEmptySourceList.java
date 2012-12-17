package org.orbisgis.view.geocatalog.actions;

import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.view.components.actions.DefaultAction;
import javax.swing.Icon;
import java.awt.event.ActionListener;

/**
 * Action shown only if the SourceManager contain something else than the system sources.
 * @author Nicolas Fortin
 */
public class ActionOnNonEmptySourceList extends DefaultAction {
    /**
     * @param actionId Action identifier, should be unique for ActionCommands
     * @param actionLabel I18N label short label
     * @param actionToolTip I18N tool tip text
     * @param icon Icon
     * @param actionListener Fire the event to this listener
     */
    public ActionOnNonEmptySourceList(String actionId, String actionLabel,String actionToolTip, Icon icon,ActionListener actionListener) {
        super(actionId, actionLabel, actionToolTip, icon, actionListener, null);
    }

    @Override
    public boolean isEnabled() {
        DataManager dm = Services.getService(DataManager.class);
        SourceManager dr = dm.getSourceManager();
        return super.isEnabled() && !dr.isEmpty(true);
    }
}
