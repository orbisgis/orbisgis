/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.mainframe.impl;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferencePolicyOption;
import org.apache.felix.scr.annotations.Service;
import org.orbisgis.commons.events.OGVetoableChangeSupport;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.mainframe.api.MainFrameAction;
import org.orbisgis.mainframe.api.MainStatusBar;
import org.orbisgis.mainframe.api.MainWindow;
import org.orbisgis.mainframe.icons.MainFrameIcon;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.wkguiapi.ViewWorkspace;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Main window that contain all docking panels.
 */
@Component(metatype = true)
@Service({MainWindow.class})
public class MainFrame extends JFrame implements MainWindow {
    private static final I18n I18N = I18nFactory.getI18n(MainFrame.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private static final String WKDIALOG_BUNDLE_NAME = "workspace-gui";

    @Reference(referenceInterface = MainFrameAction.class, bind = "addMenuItem", unbind = "removeMenuItem",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    private ActionCommands actions = new ActionCommands();
    private JMenuBar menuBar = new JMenuBar();
    private MainFrameStatusBar mainFrameStatusBar = new MainFrameStatusBar();
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private LogListenerOverlay messageOverlay = new LogListenerOverlay();
    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;
    @Property(intValue = DEFAULT_WIDTH, name = "width")
    private Integer width = DEFAULT_WIDTH;
    @Property(intValue = DEFAULT_HEIGHT, name = "height")
    private Integer height = DEFAULT_HEIGHT;
    private OGVetoableChangeSupport vetoableChangeSupport = new OGVetoableChangeSupport(this);

    @Reference(bind = "setCoreWorkspace", unbind = "unsetCoreWorkspace")
    private CoreWorkspace coreWorkspace;
    @Reference(bind = "setViewWorkspace", unbind = "unsetViewWorkspace")
    private ViewWorkspace viewWorkspace;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY,policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY, bind = "setDockingManager", unbind = "unsetDockingManager")
    private DockingManager dockingManager = null;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC,
            bind = "setLogReaderService", unbind = "unsetLogReaderService")
    private LogReaderService logReaderService;
    private JMenu panelList;
    // The main window can stop the framework bundle
    private BundleContext bundleContext;
    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC,
            bind = "setConfigurationAdmin", unbind = "unsetConfigurationAdmin")
    private ConfigurationAdmin configurationAdmin;


    /**
     * Creates a new frame. The content of the frame is not created by
     * this constructor, clients must call {@link #init}.
     */
    public MainFrame() {
        setIconImage(MainFrameIcon.getIconImage("orbisgis"));
        add(new JLayer<>(mainPanel, messageOverlay));
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Activate
    public void activate(BundleContext bundleContext, Map properties) {
        this.bundleContext = bundleContext;
        modified(properties);
        init();
        try {
            GraphicsDevice device = GraphicsEnvironment.
                    getLocalGraphicsEnvironment().getDefaultScreenDevice();
            Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
            setLocation(screenBounds.x + screenBounds.width / 2 - getWidth() / 2, screenBounds.y +
                    screenBounds.height / 2 - getHeight() / 2);
        } catch (Throwable ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        // Very ugly, heritage from monolithic architecture
        UIFactory.setMainFrame(this);
        UIFactory.setDefaultImageIcon(MainFrameIcon.getIcon("orbisgis"));        // Load SIF properties
        try {
            UIFactory.loadState(new File(viewWorkspace.getSIFPath()));
        } catch (IOException ex) {
            LOGGER.error(I18N.tr("Error while loading dialogs informations."), ex);
        }

        //When the user ask to close OrbisGis it call
        //the shutdown method here,
        // Link the Swing Events with the MainFrame event
        //Thanks to EventHandler we don't have to build a listener class
        addWindowListener(EventHandler.create(WindowListener.class, //The listener class
                this, //The event target object
                "onMainWindowClosing",//The event target method to call
                null, //the event parameter to pass(none)
                "windowClosing")); //The listener method to use
        setVisible(true);
    }

    public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    public void unsetConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = null;
    }

    @Deactivate
    public void deactivate() {
        // Very ugly, heritage from monolithic architecture
        UIFactory.setMainFrame(null);
        this.bundleContext = null;
        dispose();
    }

    @Modified
    public void modified(Map<String, ?> properties) {
        if(properties != null) {
            width = (Integer)properties.get("width");
            height = (Integer)properties.get("height");
            if(width!= null && height != null && (width!=getWidth() || height != getHeight())) {
                setSize(width, height);
            }
        }
    }

    /**
     * @return True if the window can be closed
     */
    private boolean closeWindowSaveState() {
        try {
            vetoableChangeSupport.fireVetoableChange(WINDOW_VISIBLE, true, false);
        } catch (PropertyVetoException ex) {
            // Cancel exit
            return false;
        }
        firePropertyChange(WINDOW_VISIBLE, true, false);
        if(dockingManager != null) {
            dockingManager.saveLayout();
        }
        try {
            UIFactory.saveState(new File(viewWorkspace.getSIFPath()));
        } catch (IOException ex) {
            LOGGER.error(I18N.tr("Error while saving dialogs informations."), ex);
        }
        if(configurationAdmin != null) {
            try {
                Configuration configuration = configurationAdmin.getConfiguration(MainFrame.class.getName());
                Dictionary<String, Object> props = configuration.getProperties();
                if(props == null) {
                    props = new Hashtable<>();
                }
                props.put("width", getWidth());
                props.put("height", getHeight());
                configuration.update(props);
            } catch (IOException ex) {
                LOGGER.error(I18N.tr("Cannot save main window configuration"));
            }
        }
        return true;
    }

    /**
     * User close the window
     */
    public void onMainWindowClosing() {
        if(closeWindowSaveState()) {
            // Hide window for fastest close
            mainPanel.setVisible(false);
            // Stop application
            try {
                if (bundleContext != null) {
                    bundleContext.getBundle(0).stop();
                }
            } catch (BundleException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }

    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        setTitle(I18N.tr("OrbisGIS version {0} {1} {2}", getVersion(coreWorkspace), coreWorkspace.getVersionQualifier
                (), Locale.getDefault().getCountry()));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mainFrameStatusBar.init(coreWorkspace);
        JButton btnChangeWorkspace = new CustomButton(MainFrameIcon.getIcon("application_go"));
        btnChangeWorkspace.setToolTipText(I18N.tr("Switch to another workspace"));
        btnChangeWorkspace.addActionListener(EventHandler.create(ActionListener.class,this,"onChangeWorkspace"));
        mainFrameStatusBar.addWorkspaceBarComponent(btnChangeWorkspace);
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {

    }

    /**
     * @param viewWorkspace View workspace instance
     */
    public void setViewWorkspace(ViewWorkspace viewWorkspace) {
        this.viewWorkspace = viewWorkspace;
    }

    /**
     * @param viewWorkspace View workspace instance
     */
    public void unsetViewWorkspace(ViewWorkspace viewWorkspace) {
        this.viewWorkspace = null;
    }

    @Override
    protected void addImpl(java.awt.Component comp, Object constraints, int index) {
        if (mainPanel == null || comp instanceof JLayer) {
            super.addImpl(comp, constraints, index);
        } else {
            mainPanel.add(comp, constraints, index);
        }
    }

    // Use optional to avoid deadlock of service activation. (MainFrame launch first then DockingManager)
    public void setDockingManager(DockingManager dockingManager) {
        // Add configure menu
        actions.addAction(new DefaultAction(MainFrameAction.MENU_CONFIGURE, I18N.tr("&Configuration"), MainFrameIcon
                .getIcon("preferences-system"), EventHandler.create(ActionListener.class, this,
                "onMenuShowPreferences")).setParent(MainFrameAction.MENU_TOOLS));
        // Add window list menu
        panelList = dockingManager.getCloseableDockableMenu();
        menuBar.add(panelList);
        this.dockingManager = dockingManager;
    }

    public void unsetDockingManager(DockingManager dockingManager) {
        menuBar.remove(panelList);
        this.dockingManager = null;
    }

    public void addMenuItem(MainFrameAction mainFrameAction) {
        actions.addActionFactory(mainFrameAction, this);
    }

    public void removeMenuItem(MainFrameAction mainFrameAction) {
        actions.removeActionFactory(mainFrameAction);
    }

    public void init() {
        initActions();
        // Add actions in menu bar
        actions.registerContainer(menuBar);
        this.setJMenuBar(menuBar);
        getContentPane().add(mainFrameStatusBar, BorderLayout.SOUTH);
    }

    public static String getVersion(CoreWorkspace coreWorkspace) {
        if (coreWorkspace.getVersionRevision() != 0) {
            return coreWorkspace.getVersionMajor() + "." + coreWorkspace.getVersionMinor();
        } else {
            return coreWorkspace.getVersionMajor() + "." + coreWorkspace.getVersionMinor() + "." + coreWorkspace
                    .getVersionRevision();
        }
    }

    public void setLogReaderService(LogReaderService logReaderService) {
        logReaderService.addLogListener(messageOverlay);
    }

    public void unsetLogReaderService(LogReaderService logReaderService) {
        logReaderService.removeLogListener(messageOverlay);
    }

    /**
     * Create the built-ins menu items
     */
    private void initActions() {
        actions.addAction(new DefaultAction(MainFrameAction.MENU_FILE, I18N.tr("&File")).setMenuGroup(true));
        actions.addAction(new DefaultAction(MainFrameAction.MENU_EXIT, I18N.tr("&Exit"), MainFrameIcon.getIcon
                ("exit"), EventHandler.create(ActionListener.class, this, "onMenuExitApplication")).setParent(MainFrameAction.MENU_FILE));
        actions.addAction(new DefaultAction(MainFrameAction.MENU_TOOLS, I18N.tr("&Tools")).setMenuGroup(true));
    }

    /**
     * The user click on preferences menu item
     */
    public void onMenuShowPreferences() {
        if(dockingManager != null) {
            dockingManager.showPreferenceDialog();
        }
    }

    /**
     * The user click on exit application menu item
     */
    public void onMenuExitApplication() {
        this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public JFrame getMainFrame() {
        return this;
    }

    /**
     * Extend the mainframe with a new menu
     *
     * @param action
     */
    public void addMenu(Action action) {
        actions.addAction(action);
    }

    /**
     * Extend the main status bar with a new component
     *
     * @param component
     * @param orientation
     */
    public void addToolBarComponent(JComponent component, String orientation) {
        mainFrameStatusBar.addComponent(component, orientation);
    }

    @Override
    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(propertyName, listener);
    }

    @Override
    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

    @Override
    public MainStatusBar getStatusBar() {
        return mainFrameStatusBar;
    }

    /**
     * The user click on change workspace button
     */
    public void onChangeWorkspace() {
        if(bundleContext != null) {
            if(closeWindowSaveState()) {
                new RestartWorkspaceSelectionBundle(bundleContext, mainPanel).execute();
            }
        }
    }

    private static class RestartWorkspaceSelectionBundle extends SwingWorker {
        private BundleContext bundleContext;
        private JPanel mainPanel;
        private static final Logger LOGGER = LoggerFactory.getLogger(RestartWorkspaceSelectionBundle.class);

        public RestartWorkspaceSelectionBundle(BundleContext bundleContext, JPanel mainPanel) {
            this.bundleContext = bundleContext;
            this.mainPanel = mainPanel;
        }

        Bundle findWorkspaceGUIBundle() {
            for(Bundle bundle : bundleContext.getBundles()) {
                String name = (String)bundle.getHeaders().get("Bundle-Name");
                if(name != null && WKDIALOG_BUNDLE_NAME.equals(name)) {
                    return bundle;
                }
            }
            return null;
        }

        @Override
        protected Object doInBackground() throws Exception {
            // Retrieve workspace-gui
            Bundle wkBundle = findWorkspaceGUIBundle();
            if(wkBundle != null) {
                mainPanel.setVisible(false);
                wkBundle.stop();
                // Wait 1s
                Thread.sleep(1000);
                wkBundle.start();
            } else {
                LOGGER.error("Cannot find workspace dialog");
            }
            return null;
        }
    }
}
