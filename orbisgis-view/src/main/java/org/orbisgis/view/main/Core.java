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

import bibliothek.extension.gui.dock.DockingFramesPreference;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.RectGradientPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;
import bibliothek.gui.dock.themes.BasicTheme;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.SwingUtilities;
import org.orbisgis.base.context.main.MainContext;
import org.orbisgis.base.events.ListenerRelease;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.frames.Catalog;
import org.orbisgis.view.frames.MainFrame;
import org.orbisgis.view.translation.OrbisGISI18N;

/**
 * The core manage the look and feel for each view of the Main Frame
 * This is the main UIContext
 */
public class Core implements ComponentCollector {
    /////////////////////
    //view package
    private MainFrame mainFrame;     /*!< The main window */
    private final static Rectangle mainViewPositionAndSize = new Rectangle(20,20,800,600);/*!< Bounds of mainView, x,y and width height*/
    private DockingManager dockManager; /*!< The DockStation manager */
    /////////////////////
    //base package :
    private MainContext mainContext; /*!< The larger surrounding part of OrbisGis base */
    
    /////////////////////
    //DockingFrames package :
    private PreferenceTreeModel preferences; /*< Organizes {@link PreferenceModel}s in a tree */
    private DockFrontend frontend; /*!< link to the docking-frames */
    private LookAndFeelList lookAndFeels;    /*!< /*!< link to the docking-frames */

    public Core() {
        this.mainContext = new MainContext();
    }
    /**
     * Help DockingFrames to update the Look And Feel of all views.
     * @return All views of OrbisGis
     */
    public Collection<Component> listComponents() {
        List<Component> components = new ArrayList<Component>();
        
        components.add(mainFrame);
        components.addAll(this.dockManager.getPanels());
        return components;
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
    private void makeMainFrame() {
        mainFrame = new MainFrame();
        //When the user ask to close OrbisGis it call
        //the shutdown method here, 
        // Link the Swing Events with the MainFrame event
        //Thanks to EventHandler we don't have to build a listener class
        mainFrame.addWindowListener(EventHandler.create(
                WindowListener.class, //The listener class
                this,                 //The event target object
                "shutdown",           //The event target method to call
                null,                 //the event parameter to pass(none)
                "windowClosing"));    //The listener method to use
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
        final DockController controller = new DockController();
        
        controller.setTheme( new BasicTheme() );
        controller.getProperties().set( EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true );
        controller.getProperties().set( EclipseTheme.TAB_PAINTER, RectGradientPainter.FACTORY );
        controller.getProperties().set( FlapDockStation.BUTTON_CONTENT, ButtonContent.ICON_AND_TEXT_ONLY );

        frontend = new DockFrontend( controller, mainFrame );
	preferences = new DockingFramesPreference( controller );
        dockManager = new DockingManager(frontend,mainFrame);
        
        lookAndFeels = LookAndFeelList.getDefaultList();
        lookAndFeels.addComponentCollector( this );
        
	mainFrame.setBounds(mainViewPositionAndSize);
        dockManager.show(new Catalog(), dockManager.getRightDockStation(), null);
        dockManager.show(new Catalog(), dockManager.getSplit(), null);
        //Load GeoCatalog
        
        // Show the application when Swing will be ready
        SwingUtilities.invokeLater( new Runnable(){
                public void run(){
                        mainFrame.setVisible( true );
                        //views.getScreen().setShowing( true );
                }
        });
    }
    /**
     * Add the properties of OrbisGis view to I18n translation manager
     */
    private void initI18n() {
        // Init I18n
        I18N.addI18n("", "orbisgis", OrbisGISI18N.class);
    }
    /**
     * Free all resources in preparation of exit the software.
     */
    public void dispose() {
        //Remove all listeners created by this object
        ListenerRelease.releaseListeners(this);
        //Free UI resources
        mainFrame.dispose();
        frontend.getController().kill();
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
        finally{
            // If there is another unclosed windows, java machine may continue to run
            // In this case, the following command would exit the application
            /*
            SwingUtilities.invokeLater( new Runnable(){
                    public void run(){
                            System.exit(0);
                    }
            */
            
        }
    }
}
