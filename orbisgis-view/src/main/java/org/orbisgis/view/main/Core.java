/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.main;

import java.awt.Rectangle;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.Locale;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.core.context.main.MainContext;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.main.frames.MainFrame;
import org.orbisgis.view.map.MapEditor;
import org.orbisgis.view.output.OutputManager;
import org.orbisgis.view.toc.Toc;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The core manage the view of the application
 * This is the main UIContext
 */
public class Core {
    protected final static I18n i18n = I18nFactory.getI18n(Core.class);        
    private static final Logger LOGGER = Logger.getLogger(Core.class);
    /////////////////////
    //view package
    private MainFrame mainFrame = null;     /*!< The main window */
    private Catalog geoCatalog= null;      /*!< The GeoCatalog frame */
    private Toc toc=null;                  /*!< The map layer list frame */
    private MapEditor mapEditor=null;      /*!< The map editor-viewer frame */
    private ViewWorkspace viewWorkspace;
    private OutputManager loggerCollection;    /*!< Loggings panels */     
            
            
    private static final Rectangle MAIN_VIEW_POSITION_AND_SIZE = new Rectangle(20,20,800,600);/*!< Bounds of mainView, x,y and width height*/
    private DockingManager dockManager = null; /*!< The DockStation manager */
    
    
    /////////////////////
    //base package :
    private MainContext mainContext; /*!< The larger surrounding part of OrbisGis base */
    /**
     * Core constructor, init Model instances
     * @note Call startup() to init Swing
     */
    public Core() {
        this.mainContext = new MainContext();
        this.viewWorkspace = new ViewWorkspace(this.mainContext.getCoreWorkspace());
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
        UIFactory.setPersistencyDirectory(new File(viewWorkspace.getSIFPath()));
        UIFactory.setTempDirectory(new File(mainContext.getCoreWorkspace().getTempFolder()));
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
     * Make the toc and map frames, place them in the main window
     */
    private void makeTocAndMap() {
        toc = new Toc();
        mapEditor = new MapEditor();
        //Add the views as a new Docking Panel
        dockManager.show(mapEditor);
        dockManager.show(toc);
    }
    
    /**
     * Create the logging panels
     * All,Info,Warning,Error
     */
    private void makeLoggingPanels() {
        loggerCollection = new OutputManager();
        //Show Panel
        dockManager.show(loggerCollection.getPanel());
    }
    /**
     * Create the GeoCatalog view
     */
    private void makeGeoCatalogPanel() {
        //The geocatalog view content is read from the SourceContext
        geoCatalog = new Catalog(mainContext.getSourceContext());
        //Add the view as a new Docking Panel
        dockManager.show(geoCatalog);
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
        
        //Load the GeoCatalog
        makeGeoCatalogPanel();
        
        //Load the Map And view panels
        makeTocAndMap();
        
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
        public void run(){
                mainFrame.setVisible( true );
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
        //Remove all listeners created by this object

        //Free UI resources
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
}
