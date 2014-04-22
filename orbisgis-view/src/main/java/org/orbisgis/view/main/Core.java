/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.main;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.context.main.MainContext;
import org.orbisgis.core.plugin.PluginHost;
import org.orbisgis.core.workspace.CoreWorkspaceImpl;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.Job;
import org.orbisgis.view.background.JobQueue;
import org.orbisgis.view.docking.internals.EditorFactoryTracker;
import org.orbisgis.view.docking.internals.EditorPanelTracker;
import org.orbisgis.view.edition.EditorManagerImpl;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.view.components.actions.MenuItemServiceTracker;
import org.orbisgis.viewapi.docking.DockingManager;
import org.orbisgis.view.docking.DockingManagerImpl;
import org.orbisgis.view.docking.internals.DockingPanelTracker;
import org.orbisgis.viewapi.edition.EditableElement;
import org.orbisgis.view.edition.dialogs.SaveDocuments;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.main.bundles.BundleFromResources;
import org.orbisgis.view.main.frames.MainFrame;
import org.orbisgis.viewapi.main.frames.ext.MainFrameAction;
import org.orbisgis.viewapi.main.frames.ext.MainWindow;
import org.orbisgis.viewapi.main.frames.ext.ToolBarAction;
import org.orbisgis.view.output.OutputManager;
import org.orbisgis.view.sqlconsole.SQLConsoleFactory;
import org.orbisgis.view.table.TableEditorFactory;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.orbisgis.view.workspace.WorkspaceSelectionDialog;
import org.osgi.framework.BundleException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The core manage the view of the application.
 * This is the main UIContext
 */
public class Core {

    private static final I18n I18N = I18nFactory.getI18n(Core.class);
    private static final Logger LOGGER = Logger.getLogger("gui.popup."+Core.class);
    /**
     * Buffer to copy resource to file
     */
    private static final int BUFFER_LENGTH = 4096;
    /////////////////////
    //view package
    private EditorManagerImpl editors;         /*!< Management of editors */

    private MainFrame mainFrame;     /*!< The main window */

    private Catalog geoCatalog = null;      /*!< The GeoCatalog frame */

    private ViewWorkspace viewWorkspace;
    private OutputManager loggerCollection;    /*!< Loggings panels */

    private BackgroundManager backgroundManager;
    public static final Dimension MAIN_VIEW_SIZE = new Dimension(800, 600);/*!< Bounds of mainView, x,y and width height*/

    private DockingManager dockManager = null; /*!< The DockStation manager */

    /////////////////////
    //base package :
    private MainContext mainContext; /*!< The larger surrounding part of OrbisGis base */

    ////////////////////
    // Plugins
    private PluginHost pluginFramework;
    private DockingPanelTracker singleFrameTracker;
    private EditorFactoryTracker editorFactoryTracker;
    private EditorPanelTracker editorTracker;
    private MenuItemServiceTracker<MainWindow, ToolBarAction> toolBarTracker;

    /**
     * Core constructor, init Model instances
     *
     * @param debugMode Show additional information for debugging purposes
     * @note Call startup() to init Swing
     */
    public Core(CoreWorkspaceImpl coreWorkspace, boolean debugMode, ProgressMonitor parentProgress) throws InvocationTargetException, InterruptedException {
        ProgressMonitor progressInfo = parentProgress.startTask(I18N.tr("Loading Workspace.."),100);
        MainContext.initConsoleLogger(debugMode);
        // Declare empty main frame
        mainFrame = new MainFrame();
        //Set the main frame position and size
        mainFrame.setSize(MAIN_VIEW_SIZE);
        // Try to set the frame at the center of the default screen
        try {
            GraphicsDevice device = GraphicsEnvironment.
                    getLocalGraphicsEnvironment().getDefaultScreenDevice();
            Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
            mainFrame.setLocation(screenBounds.x + screenBounds.width / 2 - MAIN_VIEW_SIZE.width / 2,
                    screenBounds.y + screenBounds.height / 2 - MAIN_VIEW_SIZE.height / 2);
        } catch (Throwable ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        UIFactory.setMainFrame(mainFrame);
        initMainContext(debugMode, coreWorkspace);
        progressInfo.progressTo(10);
        this.viewWorkspace = new ViewWorkspace(this.mainContext.getCoreWorkspace());
        Services.registerService(ViewWorkspace.class, I18N.tr("Contains view folders path"),
                viewWorkspace);
        progressInfo.setTaskName(I18N.tr("Register GUI Sql functions.."));
        addSQLFunctions();
        progressInfo.progressTo(11);
        //Load plugin host
        progressInfo.setTaskName(I18N.tr("Load the plugin framework.."));
        startPluginHost();
        progressInfo.progressTo(18);
        progressInfo.setTaskName(I18N.tr("Connecting to the database.."));
        // Init database
        try {
            mainContext.initDataBase(coreWorkspace.getDataBaseUser(),coreWorkspace.getDataBasePassword());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
        // Init jobqueue
        initSwingJobs();
        initSIF();
        progressInfo.progressTo(20);
    }

    private void startPluginHost() {
        try {
            mainContext.startBundleHost(BundleFromResources.SPECIFIC_BEHAVIOUR_BUNDLES);
            pluginFramework = mainContext.getPluginHost();
            pluginFramework.getHostBundleContext().registerService(org.orbisgis.viewapi.workspace.ViewWorkspace.class, viewWorkspace, null);
        } catch (Exception ex) {
            LOGGER.error(I18N.tr("Loading of plugins is aborted"), ex);
        }
    }

    /**
     * Find the workspace folder or addDockingPanel a dialog to select one
     */
    private void initMainContext(boolean debugMode, CoreWorkspaceImpl coreWorkspace) throws InterruptedException, InvocationTargetException, RuntimeException {
        String workspaceFolder = coreWorkspace.getWorkspaceFolder();
        if (workspaceFolder == null) {
            File defaultWorkspace = coreWorkspace.readDefaultWorkspacePath();
            if (defaultWorkspace == null || !ViewWorkspace.isWorkspaceValid(defaultWorkspace)) {
                try {
                    PromptUserForSelectingWorkspace dial = new PromptUserForSelectingWorkspace(coreWorkspace);
                    SwingUtilities.invokeAndWait(dial);
                    if (!dial.isOk()) {
                        throw new InterruptedException("Canceled by user.");
                    }
                } catch (InvocationTargetException ex) {
                    mainFrame.dispose();
                    throw ex;
                }
            } else {
                coreWorkspace.setWorkspaceFolder(defaultWorkspace.getAbsolutePath());
            }
        }
        this.mainContext = new MainContext(debugMode, coreWorkspace, true);
    }

    /**
     * Add new menu to the OrbisGIS core
     */
    private void addCoreMenu() {
        DefaultAction def = new DefaultAction(MainFrameAction.MENU_SAVE, I18N.tr("&Save"), OrbisGISIcon.getIcon("save"),
                EventHandler.create(ActionListener.class, this, "onMenuSaveApplication"));  
        def.setParent(MainFrameAction.MENU_FILE).setBefore(MainFrameAction.MENU_EXIT);
        mainFrame.addMenu(def);
        def.setToolTipText(I18N.tr("Save the workspace"));
        JButton saveBt = new CustomButton(def);
        saveBt.setHideActionText(true);
        mainFrame.addToolBarComponent(saveBt, "align left");
    }

    private static class PromptUserForSelectingWorkspace implements Runnable {
        private CoreWorkspaceImpl coreWorkspace;
        /**
         * User do not cancel workspace selection
         */
        private boolean ok = false;

        public PromptUserForSelectingWorkspace(CoreWorkspaceImpl coreWorkspace) {
            this.coreWorkspace = coreWorkspace;
        }

        /**
         * Does the user accept a new workspace folder.
         *
         * @return False if action is canceled by user
         */
        private boolean isOk() {
            return ok;
        }

        @Override
        public void run() {
            // Ask the user to select a workspace folder
            if (WorkspaceSelectionDialog.showWorkspaceFolderSelection(null, coreWorkspace)) {
                ok = true;
            }
        }
    }


    /**
     * For UnitTest purpose
     *
     * @return The Catalog instance
     */
    public Catalog getGeoCatalog() {
        return geoCatalog;
    }

    /**
     * Init the SIF ui factory
     */
    private void initSIF() {
        UIFactory.setDefaultImageIcon(OrbisGISIcon.getIcon("mini_orbisgis"));        // Load SIF properties
        try {
            UIFactory.loadState(new File(viewWorkspace.getSIFPath()));
        } catch (IOException ex) {
            LOGGER.error(I18N.tr("Error while loading dialogs informations."), ex);
        }
    }

    /**
     *
     * @return Instance of main context
     */
    public MainContext getMainContext() {
        return mainContext;
    }
    /**
     * Instance of main frame, null if startup() has not be called.
     * @return MainFrame instance
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Create the Instance of the main frame
     */
    private void makeMainFrame() {
        mainFrame.init(pluginFramework.getHostBundleContext());
        //When the user ask to close OrbisGis it call
        //the shutdown method here,
        // Link the Swing Events with the MainFrame event
        //Thanks to EventHandler we don't have to build a listener class
        mainFrame.addWindowListener(EventHandler.create(
                WindowListener.class, //The listener class
                this, //The event target object
                "onMainWindowClosing",//The event target method to call
                null, //the event parameter to pass(none)
                "windowClosing"));    //The listener method to use
    }

    /**
     * Create the logging panels All,Info,Warning,Error
     */
    private void makeLoggingPanels() {
        loggerCollection = new OutputManager(mainContext.isDebugMode());
        //Show Panel
        dockManager.addDockingPanel(loggerCollection.getPanel());
    }

    /**
     * Create the GeoCatalog view
     */
    private void makeGeoCatalogPanel() {
        //The geo-catalog view content is read from the SourceContext
        geoCatalog = new Catalog(mainContext.getDataManager());
        // Catalog extensions
        geoCatalog.registeTrackers(pluginFramework.getHostBundleContext());
        //Add the view as a new Docking Panel
        dockManager.addDockingPanel(geoCatalog);
    }

    /**
     * Load the built-ins editors factories
     */
    private void loadEditorFactories() {
            //editors.addEditorFactory(new TocEditorFactory(pluginFramework.getHostBundleContext()));
            //editors.addEditorFactory(new MapEditorFactory(pluginFramework.getHostBundleContext(), mainContext.getDataManager(), viewWorkspace));
            editors.addEditorFactory(new SQLConsoleFactory(pluginFramework.getHostBundleContext()));
            TableEditorFactory tableEditorFactory = new TableEditorFactory();
            tableEditorFactory.setDataManager(mainContext.getDataManager());
            editors.addEditorFactory(tableEditorFactory);
            //editors.addEditorFactory(new BeanShellFrameFactory());
    }

    /**
     * Initialisation of the BackGroundManager Service
     */
    private void initSwingJobs() {
        backgroundManager = new JobQueue();
        Services.registerService(
                BackgroundManager.class,
                I18N.tr("Execute tasks in background processes, showing progress bars. Gives access to the job queue"),
                backgroundManager);
        pluginFramework.getHostBundleContext().registerService(BackgroundManager.class, backgroundManager, null);
    }

    /**
     * The user want to close the main window Then the application has to be
     * closed
     */
    public void onMainWindowClosing() {
        this.shutdown(true);
    }

    /**
     * Create the central place for editor factories. This manager will retrieve
     * panel editors and use the docking manager to addDockingPanel them
     *
     * @param dm Instance of docking manager
     */
    private void makeEditorManager(DockingManager dm) {
        editors = new EditorManagerImpl(dm);
        Services.registerService(org.orbisgis.viewapi.edition.EditorManager.class,
                I18N.tr("Use this instance to open an editable element (map,data source..)"),
                editors);
        pluginFramework.getHostBundleContext().registerService(org.orbisgis.viewapi.edition.EditorManager.class, editors, null);
        editorFactoryTracker = new EditorFactoryTracker(pluginFramework.getHostBundleContext(), editors);
        editorFactoryTracker.open();
        editorTracker = new EditorPanelTracker(pluginFramework.getHostBundleContext(), editors);
        editorTracker.open();
    }

    /**
     * Starts the application. This method creates the {@link MainFrame}, and
     * manage the Look And Feel declarations
     */
    public void startup(ProgressMonitor progress) throws Exception {
        // Show the application when Swing will be ready
        try {
            initialize(progress);
        } catch (Exception ex) {
            mainFrame.dispose();
            throw ex;
        }
        SwingUtilities.invokeLater(new ShowSwingApplication(progress));
    }

    /**
     * For unit test purpose, expose the plugin framework
     *
     * @return
     */
    public PluginHost getPluginFramework() {
        return pluginFramework;
    }

    private void initialize(ProgressMonitor parentProgress) {
        ProgressMonitor progress = parentProgress.startTask(I18N.tr("Loading the main window"), 100);
        makeMainFrame();
        progress.endTask();
        progress.setTaskName(I18N.tr("Loading docking system and frames"));
        //Initiate the docking management system
        DockingManagerImpl dockManagerImpl = new DockingManagerImpl(mainFrame);
        dockManager = dockManagerImpl;
        mainFrame.setDockingManager(dockManager);
        // Initiate the docking panel tracker
        singleFrameTracker = new DockingPanelTracker(pluginFramework.getHostBundleContext(), dockManager);
        singleFrameTracker.open();
        toolBarTracker = new MenuItemServiceTracker<MainWindow, ToolBarAction>(pluginFramework.getHostBundleContext(), ToolBarAction.class, dockManagerImpl, mainFrame);
        toolBarTracker.open();
        progress.endTask();

        //Load the log panels
        makeLoggingPanels();
        progress.endTask();

        //Load the editor factories manager
        makeEditorManager(dockManager);
        progress.endTask();

        //Load the GeoCatalog
        makeGeoCatalogPanel();
        progress.endTask();

        //Load Built-ins Editors
        loadEditorFactories();
        progress.endTask();

        progress.setTaskName(I18N.tr("Restore the former layout.."));
        //Load the docking layout and editors opened in last OrbisGis instance
        File savedDockingLayout = new File(viewWorkspace.getDockingLayoutPath());
        if (!savedDockingLayout.exists()) {
            // Copy the default docking layout
            // First OrbisGIS start
            copyDefaultDockingLayout(savedDockingLayout);
        }
        dockManager.setDockingLayoutPersistanceFilePath(viewWorkspace.getDockingLayoutPath());
        progress.endTask();
        addCoreMenu();
    }

    /**
     * Copy the default docking layout to the specified file path.
     *
     * @param savedDockingLayout
     */
    private void copyDefaultDockingLayout(File savedDockingLayout) {
        InputStream xmlFileStream = DockingManagerImpl.class.getResourceAsStream("default_docking_layout.xml");
        if (xmlFileStream != null) {
            try {
                FileOutputStream writer = new FileOutputStream(savedDockingLayout);
                try {
                    byte[] buffer = new byte[BUFFER_LENGTH];
                    for (int n; (n = xmlFileStream.read(buffer)) != -1;) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    writer.close();
                }
            } catch (FileNotFoundException ex) {
                LOGGER.error(I18N.tr("Unable to save the docking layout."), ex);
            } catch (IOException ex) {
                LOGGER.error(I18N.tr("Unable to save the docking layout."), ex);
            } finally {
                try {
                    xmlFileStream.close();
                } catch (IOException ex) {
                    LOGGER.error(I18N.tr("Unable to save the docking layout."), ex);
                }
            }
        }
    }

    /**
     * Add SQL functions to interact with OrbisGIS UI
     */
        private void addSQLFunctions() {
                //mainContext.getDataSourceFactory().getFunctionManager().addFunction(MapContext_AddLayer.class);
                //mainContext.getDataSourceFactory().getFunctionManager().addFunction(MapContext_BBox.class);
                //mainContext.getDataSourceFactory().getFunctionManager().addFunction(MapContext_ZoomTo.class);
                //mainContext.getDataSourceFactory().getFunctionManager().addFunction(MapContext_Share.class);
        }

    /**
     * Change the state of the main frame in the swing thread
     */
    private class ShowSwingApplication implements Runnable {

        ProgressMonitor progress;

        public ShowSwingApplication(ProgressMonitor progress) {
            this.progress = progress;
        }

        /**
         * Change the state of the main frame in the swing thread
         */
        @Override
        public void run() {
            try {
                mainFrame.setVisible(true);
            } finally {
                progress.setCancelled(true);
            }
        }
    }

    /**
     * Return the docking manager. This function is used by Unit Tests.
     *
     * @return The Docking Manager
     */
    public DockingManager getDockManager() {
        return dockManager;
    }

    /**
     * Free all resources allocated by this object
     */
    public void dispose() {
        //Close all running jobs
        for (Job job : backgroundManager.getActiveJobs()) {
            try {
                job.cancel();
            } catch (Throwable ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                //Cancel the next job
            }
        }

        //Free UI resources
        editors.dispose();
        geoCatalog.dispose();
        mainFrame.dispose();
        if (singleFrameTracker != null) {
            singleFrameTracker.close();
        }
        if(editorFactoryTracker != null) {
            editorFactoryTracker.close();
        }
        if(editorTracker != null) {
            editorTracker.close();
        }
        if (toolBarTracker != null) {
            toolBarTracker.close();
        }
        dockManager.dispose();
        loggerCollection.dispose();

        //Free libraries resources
        mainContext.dispose();

        UIFactory.setMainFrame(null);
    }

    /**
     * Save or discard editable element modification. Show a dialog if there is
     * at least one unsaved editable element.
     *
     * @return True if the application must cancel the close shutdown operation
     */
    private boolean isShutdownVetoed() {
        List<EditableElement> modifiedElements = new ArrayList<EditableElement>();
        Collection<EditableElement> editableElement = editors.getEditableElements();
        for (EditableElement editable : editableElement) {
            if (editable.isModified()) {
                modifiedElements.add(editable);
            }
        }
        if (!modifiedElements.isEmpty()) {
            SaveDocuments.CHOICE userChoice = SaveDocuments.showModal(mainFrame, modifiedElements);
            // If the user do not want to save the editable elements
            // Then cancel the modifications
            if (userChoice == SaveDocuments.CHOICE.SAVE_NONE) {
                for (EditableElement element : modifiedElements) {
                    element.setModified(false);
                }
            }
            return userChoice == SaveDocuments.CHOICE.CANCEL;
        } else {
            return false;
        }
    }

    private void saveSIFState() {
        // Load SIF properties
        try {
            UIFactory.saveState(new File(viewWorkspace.getSIFPath()));
        } catch (IOException ex) {
            LOGGER.error(I18N.tr("Error while saving dialogs informations."), ex);
        }
    }

    /**
     * Stops this application, closes the {@link MainFrame} and saves all
     * properties. This method is called through the
     * MainFrame.MAIN_FRAME_CLOSING event listener.
     */
    public boolean shutdown(boolean stopVM) {
        if (!isShutdownVetoed()) {
            try {
                mainFrame.setVisible(false); //Hide the main panel
                mainContext.saveStatus(); //Save the services status
                // Save dialogs status
                saveSIFState();
                // Save layout
                dockManager.saveLayout();
                this.dispose();
            } finally {
                //While public Plugins are not implemented do not close the VM in finally clause
                if(stopVM) {
                    SwingUtilities.invokeLater( new Runnable(){
                   /** If an error occur while unload resources, java machine
                    * may continue to run. In this case, the following command
                    * would terminate the application.
                    */
                    public void run(){
                            System.exit(0);
                    }
                   } );
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method to save the workspace : documents and layout
     */
    public void onMenuSaveApplication() {
        if (!isShutdownVetoed()) {
            mainContext.saveStatus(); //Save the services status
            // Save dialogs status
            saveSIFState();
            // Save layout
            dockManager.saveLayout();
            LOGGER.info(I18N.tr("The workspace has been saved."));
        } else {
            LOGGER.info(I18N.tr("The workspace hasn't been saved."));
        }
    }
}
