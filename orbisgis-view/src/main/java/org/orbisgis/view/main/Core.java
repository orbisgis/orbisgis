/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import java.awt.Rectangle;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.context.main.MainContext;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.Job;
import org.orbisgis.view.background.JobQueue;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.joblist.JobsPanel;
import org.orbisgis.view.main.frames.MainFrame;
import org.orbisgis.view.map.MapEditorFactory;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.output.OutputManager;
import org.orbisgis.view.toc.TocEditorFactory;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The core manage the view of the application
 * This is the main UIContext
 */
public class Core {
    protected static final I18n I18N = I18nFactory.getI18n(Core.class);        
    private static final Logger LOGGER = Logger.getLogger(Core.class);
    /////////////////////
    //view package
    private EditorManager editors;         /*!< Management of editors */
    private MainFrame mainFrame = null;     /*!< The main window */
    private Catalog geoCatalog= null;      /*!< The GeoCatalog frame */
    private ViewWorkspace viewWorkspace;
    private OutputManager loggerCollection;    /*!< Loggings panels */     
    private BackgroundManager backgroundManager;
            
    private static final Rectangle MAIN_VIEW_POSITION_AND_SIZE = new Rectangle(20,20,800,600);/*!< Bounds of mainView, x,y and width height*/
    private DockingManager dockManager = null; /*!< The DockStation manager */
    
    
    /////////////////////
    //base package :
    private MainContext mainContext; /*!< The larger surrounding part of OrbisGis base */
    /**
     * Core constructor, init Model instances
     * @param debugMode Show additional information for debugging purposes
     * @note Call startup() to init Swing
     */
    public Core(boolean debugMode) {
        this.mainContext = new MainContext(debugMode);
        this.viewWorkspace = new ViewWorkspace(this.mainContext.getCoreWorkspace());
        initSwingJobs();
        initSIF();
    }
    /**
     * For UnitTest purpose
     * @return The Catalog instance
     */
    public Catalog getGeoCatalog() {
        return geoCatalog;
    }
    /**
     * Init the SIF ui factory
     */
    private void initSIF() {
        UIFactory.setDefaultImageIcon(OrbisGISIcon.getIcon("mini_orbisgis"));
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
        mainFrame = new MainFrame();
        //When the user ask to close OrbisGis it call
        //the shutdown method here, 
        // Link the Swing Events with the MainFrame event
        //Thanks to EventHandler we don't have to build a listener class
        mainFrame.addWindowListener(EventHandler.create(
                WindowListener.class, //The listener class
                this,                 //The event target object
                "onMainWindowClosing",//The event target method to call
                null,                 //the event parameter to pass(none)
                "windowClosing"));    //The listener method to use
        UIFactory.setMainFrame(mainFrame);
    }
    
    /**
     * Create the logging panels
     * All,Info,Warning,Error
     */
    private void makeLoggingPanels() {
        loggerCollection = new OutputManager(mainContext.isDebugMode());
        //Show Panel
        dockManager.show(loggerCollection.getPanel());
    }
    /**
     * Create the GeoCatalog view
     */
    private void makeGeoCatalogPanel() {
        //The geocatalog view content is read from the SourceContext
        geoCatalog = new Catalog();
        //Add the view as a new Docking Panel
        dockManager.show(geoCatalog);
    }
    
    /**
     * Create the Job processing information and control panel
     */
    private void makeJobsPanel() {
            dockManager.show(new JobsPanel());
    }
    /**
     * Load the built-ins editors factories
     */
    private void loadEditorFactories() {
            editors.addEditorFactory(new TocEditorFactory());
            editors.addEditorFactory(new MapEditorFactory());
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
    }
    /**
     * The user want to close the main window
     * Then the application has to be closed
     */
    public void onMainWindowClosing() {
        this.shutdown();
    }
    /**
    * Starts the application. This method creates the {@link MainFrame},
    * and manage the Look And Feel declarations
    */
    public void startup(){
        if(mainFrame!=null) {
            return;//This method can't be called twice
        }
        initI18n();        
        
        makeMainFrame();
        
        //Initiate the docking management system
        dockManager = new DockingManager(mainFrame);
        mainFrame.setDockingManager(dockManager);
        
        //Set the main frame position and size
	mainFrame.setBounds(MAIN_VIEW_POSITION_AND_SIZE);
        
        //Load the log panels
        makeLoggingPanels();
        
        //Load the Job Panel
        makeJobsPanel();
        
        //Load the editor factories manager
        editors = new EditorManager(dockManager);
        
        //Load the GeoCatalog
        makeGeoCatalogPanel();
        
        //Load the Map And view panels
        //makeTocAndMap();
        //Load Built-ins Editors
        loadEditorFactories();
        
        //Debug create serialisation of panels
        
        //Load the docking layout and editors opened in last OrbisGis instance
        dockManager.setDockingLayoutPersistanceFilePath(viewWorkspace.getDockingLayoutPath());
        
        // Show the application when Swing will be ready
        SwingUtilities.invokeLater( new ShowSwingApplication());
    }
    
    /**
     * Change the state of the main frame in the swing thread
     */
    private class ShowSwingApplication implements Runnable {
        /**
        * Change the state of the main frame in the swing thread
        */
        @Override
        public void run(){
                mainFrame.setVisible( true );                
                backgroundManager.backgroundOperation(new ReadMapContextProcess());
        }
    }
    /**
     * Return the docking manager. This function is used by Unit Tests.
     * @return The Docking Manager
     */
    public DockingManager getDockManager() {
        return dockManager;
    }
    /**
     * Add the properties of OrbisGis view to I18n translation manager
     */
    private void initI18n() {
        // Init I18n
    }
    /**
     * Free all resources allocated by this object
     */
    public void dispose() {
        //Close all running jobs
        for(Job job : backgroundManager.getActiveJobs()) {
                try {
                        job.cancel();
                } catch (Throwable ex) {
                        LOGGER.error(ex);
                        //Cancel the next job
                }
        }
        //Remove all listeners created by this object

        //Free UI resources
        editors.dispose();
        geoCatalog.dispose();
        mainFrame.dispose();
        dockManager.dispose();
        loggerCollection.dispose();
        
        //Free libraries resources
        mainContext.dispose();
        
    }
    /**
    * Stops this application, closes the {@link MainFrame} and saves
    * all properties if the application is not in a {@link #isSecure() secure environment}.
    * This method is called through the MainFrame.MAIN_FRAME_CLOSING event listener.
    */
    public void shutdown(){
        try{
            mainContext.saveStatus(); //Save the services status
            this.dispose();
        }
        finally {      
            //While Plugins are not implemented do not close the VM in finally clause
            //SwingUtilities.invokeLater( new Runnable(){
            //   /** If an error occuring while unload resources, java machine
            //    * may continue to run. In this case, the following command
            //    * would terminate the application.
            //    */
            //    public void run(){
            //            System.exit(0);
            //    }
            //} );
        }        
    }
    private class ReadMapContextProcess implements BackgroundJob {

                @Override
                public void run(ProgressMonitor pm) {                        
                        //Create an empty map context
                        MapContext mapContext = new OwsMapContext();

                        //Load the map context
                        File mapContextFolder = new File(viewWorkspace.getMapContextPath());
                        if(!mapContextFolder.exists()) {
                                mapContextFolder.mkdir();
                        }
                        File mapContextFile = new File(viewWorkspace.getMapContextPath()+File.separator+"mapcontext");
                        if(mapContextFile.exists()) {
                                try {
                                        mapContext.read(new FileInputStream(mapContextFile));
                                } catch (FileNotFoundException ex) {
                                        LOGGER.error(I18N.tr("The saved map context cannot be read, starting with an empty map context."),ex);
                                } catch (IllegalArgumentException ex) {
                                        LOGGER.error(I18N.tr("The saved map context cannot be read, starting with an empty map context."),ex);
                                }
                        }
                        MapElement editableMap = new MapElement(mapContext,mapContextFile);
                        editableMap.open(null);
                        editors.openEditable(editableMap); 
                }

                @Override
                public String getTaskName() {
                        return I18N.tr("Open the map context");
                }
            
    }
}
