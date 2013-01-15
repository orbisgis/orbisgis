package org.orbisgis.omanager;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.orbisgis.omanager.ui.MainPanel;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.main.frames.ext.MainFrameAction;
import org.orbisgis.view.main.frames.ext.MainWindow;
import org.osgi.framework.BundleContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Nicolas Fortin
 */
public class ManagerMenuFactory implements MainFrameAction {
    private static final Logger LOGGER = Logger.getLogger("gui."+ManagerMenuFactory.class);
    public static final String MENU_MANAGE_PLUGINS = "A_MANAGE_PLUGINS";
    private static final I18n I18N = I18nFactory.getI18n(ManagerMenuFactory.class);
    private BundleContext bundleContext;
    private MainPanel mainPanel;
    private MainWindow target; // There is only one main window in the application, the it can be stored here.
    /**
     * @param bundleContext Used to track OSGi bundle repository service, and to manage bundles.
     */
    public ManagerMenuFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        LOGGER.info("Init manager");
    }

    public List<Action> createActions(MainWindow target) {
        this.target = target;
        List<Action> actions = new ArrayList<Action>();
        actions.add(new DefaultAction(MENU_MANAGE_PLUGINS,I18N.tr("&Manage plug-ins"),
                new ImageIcon(ManagerMenuFactory.class.getResource("panel_icon.png")),
                EventHandler.create(ActionListener.class,this,"showManager")).setParent(MENU_TOOLS).setInsertFirst(true));
        return actions;
    }

    /**
     * Make and show the plug-ins manager
     */
    public void showManager() {
        LOGGER.info("Show manager..");
        if(mainPanel==null) {
            mainPanel = new MainPanel(target.getMainFrame());
        }
        mainPanel.setModal(false);
        mainPanel.setVisible(true);
    }

    public void disposeActions(MainWindow target, List<Action> actions) {
        // Close the Dialog if created
        if(mainPanel != null) {
            mainPanel.dispose();
        }
    }
}
