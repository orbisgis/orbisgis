package org.orbisgis.omanager;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.main.frames.ext.MainFrameAction;
import org.orbisgis.view.main.frames.ext.MainWindow;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Nicolas Fortin
 */
public class ManagerMenuFactory implements MainFrameAction {
    public static final String A_MANAGE_PLUGINS = "A_MANAGE_PLUGINS";
    private static final I18n I18N = I18nFactory.getI18n(ManagerMenuFactory.class);



    public List<Action> createActions(MainWindow target) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new DefaultAction(A_MANAGE_PLUGINS,I18N.tr("&Manage plug-ins"),
                new ImageIcon(ManagerMenuFactory.class.getResource("panel_icon.png")),
                EventHandler.create(ActionListener.class,this,"showManager")).setParent(MENU_TOOLS));
        return actions;
    }

    /**
     * Make and show the plug-ins manager
     */
    public void showManager() {

    }

    public void disposeActions(MainWindow target, List<Action> actions) {

    }
}
