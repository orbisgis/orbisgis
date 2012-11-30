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
package org.orbisgis.view.docking;

import bibliothek.extension.gui.dock.preference.PreferenceTreeDialog;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.common.menu.CLookAndFeelMenuPiece;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.util.PathCombiner;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JMenu;
import org.apache.log4j.Logger;
import org.orbisgis.view.docking.internals.ApplicationRessourceDecorator;
import org.orbisgis.view.docking.internals.CustomMultipleCDockable;
import org.orbisgis.view.docking.internals.CustomPanelHolder;
import org.orbisgis.view.docking.internals.CustomSingleCDockable;
import org.orbisgis.view.docking.internals.DockingArea;
import org.orbisgis.view.docking.internals.InternalCommonFactory;
import org.orbisgis.view.docking.internals.OrbisGISView;
import org.orbisgis.view.docking.preferences.OrbisGISPreferenceTreeModel;
import org.orbisgis.view.docking.preferences.editors.UserInformationEditor;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.osgi.framework.BundleContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
/**
 * Initialize and manage CControl.
 * 
 * This manager can save and load emplacement of views in XML.
 */
public final class DockingManagerImpl implements DockingManager {        

        private JFrame owner;
        private SingleCDockableListMenuPiece dockableMenuTracker;
        private static final I18n I18N = I18nFactory.getI18n(DockingManagerImpl.class);
        private static final Logger LOGGER = Logger.getLogger(DockingManagerImpl.class);
        File dockingState=null;
        private static final boolean DOCKING_STATE_XML = true;
        private CControl commonControl; /*!< link to the docking-frames */
        //Docking Area (DockingFrames feature named WorkingArea)
        private Map<String,DockingArea> dockingAreas = new HashMap<String,DockingArea>();
	/** the available preferences for docking frames */
        private PreferenceTreeModel preferences;
        private DockingPanelTracker singleFrameTracker;

	/**
	 * Creates the new manager
	 * @param owner the window used as parent for all dialogs
	 */
	public DockingManagerImpl( JFrame owner,BundleContext context){
                this.owner = owner;
                commonControl = new CControl(owner);
                commonControl.addControlListener(new DockingListener());
                dockableMenuTracker = new SingleCDockableListMenuPiece( commonControl);
                //Retrieve the Docking Frames Preferencies
                preferences = new OrbisGISPreferenceTreeModel( commonControl,PathCombiner.APPEND);
                commonControl.setPreferenceModel(preferences);

                //DEFAULT property of a view
		commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, I18N.tr("Docked Window") );
		commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, OrbisGISIcon.getIcon("mini_orbisgis") );
                //StackDockStation will contain all instances of ReservedDockStation
                owner.add(commonControl.getContentArea());
                singleFrameTracker = new DockingPanelTracker(context, this);
                singleFrameTracker.open();
	}
        /**
         * @return the managed frame
         */
        public JFrame getOwner() {
                return owner;
        }

        @Override
        public void removeDockingPanel(String dockId) {
                commonControl.removeSingleDockable(dockId);
        }
        
        /**
         * 
         * @return The look and feel menu
         */
        public JMenu getLookAndFeelMenu() {
            RootMenuPiece laf = new RootMenuPiece(I18N.tr("&Look And Feel"), false, new CLookAndFeelMenuPiece( commonControl ));
            return laf.getMenu();
        }
        /**
         * 
         * @return The menu that shows items declared in the docking
         */
        public JMenu getCloseableDockableMenu() {
            RootMenuPiece laf = new RootMenuPiece(I18N.tr("&Windows"), false,dockableMenuTracker);
            return laf.getMenu();
        }
        
        /**
         * Serialise the entire panels workspace
         */
        private void readXML() {
                XElement backup = new XElement("layout");
                commonControl.writeXML(backup);
                try {
                        // Read the entire XML file in memory
                        BufferedInputStream in = new BufferedInputStream( new FileInputStream( dockingState ));
                        XElement element = XIO.readUTF( in );
                        in.close();
                        // Use the docking frame serialisation
                        commonControl.readXML(element);
                        // Use OrbisGIS serialisation
                        for(CustomPanelHolder panel : getPanelDecorator()) {
                                // Only SingleCDockable is not managed for custom layout information
                                if(panel instanceof CustomSingleCDockable) {
                                        CustomSingleCDockable scdockable = (CustomSingleCDockable)panel;
                                        DockingPanelLayout layout = scdockable.getDockingPanel().getDockingParameters().getLayout();
                                        if(layout != null) {
                                                commonControl.getResources().put(scdockable.getUniqueId(), new ApplicationRessourceDecorator(layout));
                                        }
                                }                                
                        }
                } catch (IOException ex) {
                        LOGGER.error(I18N.tr("Unable to load the docking layout."), ex);
                        commonControl.readXML(backup);
                } catch (IllegalArgumentException ex) {
                        LOGGER.error(I18N.tr("Unable to load the docking layout."), ex);
                        commonControl.readXML(backup);
                }
        }

        private void writeXML() throws IOException {
                // Make an empty XML tree
                XElement root = new XElement( "root" );
                
                // Use the docking frame serialisation
                commonControl.writeXML(root);
                // Save the tree in the file
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( dockingState ));
                XIO.writeUTF( root, out );
                out.close();
                
        }
        /**
         * Load the docking layout 
         */
        private void loadLayout() {
            if(dockingState!=null) {
                if(dockingState.exists()) {
                    if(DOCKING_STATE_XML) {
                            readXML();
                    }
                }
            }            
        }
        /**
         * Save the docking layout
         */
        public void saveLayout() {
            if(dockingState!=null) {
                try {
                    if(DOCKING_STATE_XML) {
                            writeXML();
                    }
                } catch (IOException ex) {
                    LOGGER.error(I18N.tr("Unable to save the docking layout."), ex);
                }    
            }
        }
        /**
         * Show the preference dialog, on the owner,
         * with at least the preference model of DockingFrames
         */
        public void showPreferenceDialog() {
            PreferenceTreeDialog dialog = new PreferenceTreeDialog( preferences, true );
            //Add custom editors
            dialog.setEditorFactory(UserInformationEditor.TYPE_USER_INFO, UserInformationEditor.FACTORY);
            //Show dialog
            dialog.openDialog( owner, true );
        }
        /**
         * The multiple instances panels can be shown at the next start of application
         * if their factory is registered 
         * before loading the layout {@link setDockingStateFile}
         * @param factoryName
         * @param factory  
         */
        public void registerPanelFactory(String factoryName,DockingPanelFactory factory) {
            InternalCommonFactory dockingFramesFactory = new InternalCommonFactory(factory,commonControl);
            commonControl.addMultipleDockableFactory(factoryName, dockingFramesFactory);
        }
        
        /**
         * Free docking resources and save the layout
         */
        public void dispose() {
            saveLayout();
            singleFrameTracker.close();
            commonControl.destroy();
        }

        /**
         * For UnitTest purpose only
         * @param panel 
         * @return DefaultCDockable instance, null if not exists
         */
        public CDockable getDockable(DockingPanel panel) {
                int count = commonControl.getCDockableCount();
                for(int i=0; i<count; i++) {
                        CDockable libComponent = commonControl.getCDockable(i);
                        DockingPanel cPanel = ((CustomPanelHolder)libComponent).getDockingPanel();
                        if(cPanel.equals(panel)) {
                                return libComponent;
                        }
                }
                return null;
        }
        /**
         * Get the intermediate panels
         * @return 
         */
        private List<CustomPanelHolder> getPanelDecorator() {
                List<CustomPanelHolder> activePanel = new ArrayList<CustomPanelHolder>();
                int count = commonControl.getCDockableCount();
                for(int i=0; i<count; i++) {
                        activePanel.add(((CustomPanelHolder)commonControl.getCDockable(i)));
                }
                return activePanel;                
        }
        /**
         * Get the current opened panels
         * @return 
         */
        public List<DockingPanel> getPanels() {
                List<DockingPanel> activePanel = new ArrayList<DockingPanel>();
                for(CustomPanelHolder holder : getPanelDecorator()) {
                        activePanel.add(holder.getDockingPanel());
                }
                return activePanel;
        }
        
        
        /**
         * DockingManagerImpl will load and save the panels layout
         * in the specified file. Load the layout if the file exists.
         * @param dockingStateFilePath Destination of the default persistence file
         */
        public void setDockingLayoutPersistanceFilePath(String dockingStateFilePath) {
            this.dockingState = new File(dockingStateFilePath);
            loadLayout();
        }

        /**
         * Create a new dockable corresponding to this layout
         * @param factoryId The factory id registerPanelFactory:factoryName
         * @param panelLayout 
         */
        public void show(String factoryId, DockingPanelLayout panelLayout) {
                MultipleCDockableFactory<?, ?> factory = commonControl.getMultipleDockableFactory(factoryId);
                if (factory != null && factory instanceof InternalCommonFactory) {
                        InternalCommonFactory iFactory = (InternalCommonFactory) factory;
                        CustomMultipleCDockable dockItem = iFactory.read(panelLayout);
                        if (dockItem != null) {
                                commonControl.addDockable(dockItem);
                        }
                }
        }
        @Override
        public String addDockingPanel( DockingPanel frame) {
            return show(frame);
        }
        
	/**
	 * Shows a view at the given location as child
	 * of <code>root</code>.
	 * @param frame the <code>DockingPanel</code> for which a view should be opened
	 * @param root the preferred parent, might be <code>null</code>
	 * @param location the preferred location, relative to <code>root</code>. Might
	 * be <code>null</code>.
         * @return Dockable unique ID
	 */
	private String show(DockingPanel frame){
            //Create the DockingFrame item
            if(frame.getDockingParameters().getName().isEmpty()) {
                //If the dev doesn't define a name on the panel
                //We set the name as the name of the class
                frame.getDockingParameters().setName(frame.getClass().getCanonicalName());
            }
            SingleCDockable dockItem = OrbisGISView.createSingle( frame, commonControl );
            //Place the item in a dockstation
            String restrictedAreaName = frame.getDockingParameters().getDockingArea();
            if(!restrictedAreaName.isEmpty()) {
                //This item is restricted to an area
                DockingArea dockArea = dockingAreas.get(restrictedAreaName);
                if(dockArea==null) {
                    dockArea = new DockingArea(commonControl.createWorkingArea(restrictedAreaName));
                    dockArea.getWorkingArea().setVisible(true);
                    dockingAreas.put(restrictedAreaName,dockArea);                        
                }
                dockItem.setWorkingArea(dockArea.getWorkingArea());
                dockArea.getWorkingArea().add(dockItem);
            }           
            commonControl.addDockable(dockItem);
            return dockItem.getUniqueId();
	}
        /**
         * When a dockable is added, this listener
         * read the OrbisGIS DockingPanelParameters of the panel
         * and apply to the DockingFrames panel instance
         */
        private class DockingListener implements CControlListener {

                @Override
                public void added(CControl control, CDockable dockable) {
                        if(dockable instanceof CustomPanelHolder && dockable instanceof DefaultCDockable) {
                                CustomPanelHolder dockItem = (CustomPanelHolder)dockable;
                                OrbisGISView.setListeners(dockItem.getDockingPanel(), (DefaultCDockable)dockable);
                        } else {
                                LOGGER.error("Unknown dockable, not an OrbisGIS approved component.");
                        }
                }

                @Override
                public void removed(CControl control, CDockable dockable) {
                        
                }

                @Override
                public void opened(CControl control, CDockable dockable) {
                        
                }

                @Override
                public void closed(CControl control, CDockable dockable) {
                        
                }
                
        }
}
