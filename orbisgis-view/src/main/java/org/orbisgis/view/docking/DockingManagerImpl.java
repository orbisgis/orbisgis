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
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.common.menu.CLookAndFeelMenuPiece;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;
import bibliothek.gui.dock.toolbar.location.CToolbarLocation;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.PaintableComponent;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.util.PathCombiner;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;

import org.apache.log4j.Logger;
import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.components.actions.ActionsHolder;
import org.orbisgis.view.docking.internals.*;
import org.orbisgis.view.docking.internals.actions.CActionHolder;
import org.orbisgis.view.docking.internals.actions.ToolBarActions;
import org.orbisgis.view.docking.internals.actions.ToolBarItem;
import org.orbisgis.view.docking.preferences.OrbisGISPreferenceTreeModel;
import org.orbisgis.view.docking.preferences.editors.UserInformationEditor;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.util.MenuCommonFunctions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
/**
 * Initialize and manage CControl.
 * 
 * This manager can save and load emplacement of views in XML.
 */
public final class DockingManagerImpl implements DockingManager, ActionsHolder {

        private JFrame owner;
        private SingleCDockableListMenuPiece dockableMenuTracker;
        private static final I18n I18N = I18nFactory.getI18n(DockingManagerImpl.class);
        private static final Logger LOGGER = Logger.getLogger(DockingManagerImpl.class);
        private File dockingState=null;
        private static final boolean DOCKING_STATE_XML = true;
        private CControl commonControl; /*!< link to the docking-frames */
        //Docking Area (DockingFrames feature named WorkingArea)
        private Map<String,DockingArea> dockingAreas = new HashMap<String,DockingArea>();
	    /** the available preferences for docking frames */
        private PreferenceTreeModel preferences;
        private CToolbarContentArea area;
        // Map for the default lo
        // Key: Logical Value: Last set location
        private Map<String,CLocation> lastToolBarLocation = new HashMap<String, CLocation>();
        // Action provided to this DockingManager
        private List<Action> addedToolBarActions = new LinkedList<Action>();
        /**
         * Creates the new manager
         * @param owner the window used as parent for all dialogs
         */
	    public DockingManagerImpl( JFrame owner){
                this.owner = owner;
                commonControl = new CControl(owner);
                commonControl.addControlListener(new DockingListener());
                dockableMenuTracker = new SingleCDockableListMenuPiece( commonControl);
                //Retrieve the Docking Frames Preferencies
                preferences = new OrbisGISPreferenceTreeModel( commonControl,PathCombiner.APPEND);
                commonControl.setPreferenceModel(preferences);

                //DEFAULT property of a view
                // commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, I18N.tr("Docked Window") );
                commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, OrbisGISIcon.getIcon("mini_orbisgis") );
                commonControl.getController().getThemeManager().setBackgroundPaint(ThemeManager.BACKGROUND_PAINT + ".station.toolbar.container",new ToolBarBackGround());
                        //StackDockStation will contain all instances of ReservedDockStation
                area = new CToolbarContentArea( commonControl, "base" );
                commonControl.addStationContainer( area );
                owner.add(area);
	    }
        /**
         * @return the managed frame
         */
        @Override
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
        @Override
        public JMenu getLookAndFeelMenu() {
            RootMenuPiece laf = new RootMenuPiece(I18N.tr("&Look And Feel"), false, new CLookAndFeelMenuPiece( commonControl ));
            JMenu menu = laf.getMenu();
            MenuCommonFunctions.setMnemonic(menu);
            return menu;
        }
        /**
         * 
         * @return The menu that shows items declared in the docking
         */
        @Override
        public JMenu getCloseableDockableMenu() {
            RootMenuPiece windows = new RootMenuPiece(I18N.tr("&Windows"), false,dockableMenuTracker);
            JMenu menu = windows.getMenu();
            MenuCommonFunctions.setMnemonic(menu);
            return menu;
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
                                                commonControl.getResources().put(scdockable.getUniqueId(), new ApplicationRessourceDecorator(new DockingPanelLayoutDecorator(layout)));
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
                // Check that non closable frame are shown
                for(DockingPanel panel : getPanels()) {
                        DockingPanelParameters params = panel.getDockingParameters();
                        if(!params.isCloseable() && !params.isVisible()) {
                                params.setVisible(true);
                        }
                }
                // Check that all toolbars are visible
                CToolbarLocation defaultLocation = area.getNorthToolbar().getStationLocation().group(0).toolbar(0,0);
                int itemId = 0;
                for(ToolBarItem item : getToolBarItems()) {
                        if(!item.isVisible()) {
                                // Reset location
                                setLocation(item,defaultLocation.item(itemId++));
                                item.setVisible(true);
                                // Reset style
                                item.setResizeRequest(ToolBarItem.TOOLBAR_ITEM_SIZE, true);
                        }
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
        @Override
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
        @Override
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
         * before loading the layout
         * @param factoryName
         * @param factory  
         */
        @Override
        public void registerPanelFactory(String factoryName,DockingPanelFactory factory) {
            InternalCommonFactory dockingFramesFactory = new InternalCommonFactory(factory,commonControl);
            commonControl.addMultipleDockableFactory(factoryName, dockingFramesFactory);
        }
        
        /**
         * Free docking resources and save the layout
         */
        @Override
        public void dispose() {
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
                List<CustomPanelHolder> activePanel = new LinkedList<CustomPanelHolder>();
                int count = commonControl.getCDockableCount();
                for(int i=0; i<count; i++) {
                        CDockable dockable = commonControl.getCDockable(i);
                        if(dockable instanceof CustomPanelHolder) {
                                activePanel.add(((CustomPanelHolder)dockable));
                        }
                }
                return activePanel;                
        }

        private List<ToolBarItem> getToolBarItems() {
                List<ToolBarItem> toolBarItemList = new LinkedList<ToolBarItem>();
                int count = commonControl.getCDockableCount();
                for(int i=0; i<count; i++) {
                        CDockable dockable = commonControl.getCDockable(i);
                        if(dockable instanceof ToolBarItem) {
                                toolBarItemList.add((ToolBarItem)dockable);
                        }
                }
                return toolBarItemList;
        }
        /**
         * Get the current opened panels
         * @return 
         */
        @Override
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
        @Override
        public void setDockingLayoutPersistanceFilePath(String dockingStateFilePath) {
            this.dockingState = new File(dockingStateFilePath);
            loadLayout();
        }

        /**
         * Create a new dockable corresponding to this layout
         * @param factoryId The factory id registerPanelFactory:factoryName
         * @param panelLayout 
         */
        @Override
        public void show(String factoryId, DockingPanelLayout panelLayout) {
                MultipleCDockableFactory<?, ?> factory = commonControl.getMultipleDockableFactory(factoryId);
                if (factory != null && factory instanceof InternalCommonFactory) {
                        InternalCommonFactory iFactory = (InternalCommonFactory) factory;
                        CustomMultipleCDockable dockItem = iFactory.read(new DockingPanelLayoutDecorator(panelLayout));
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

        @Override
        public void addAction(Action action) {
                addToolbarItem(action);
        }

        @Override
        public void addActions(List<Action> newActions) {
                addedToolBarActions.addAll(newActions);
                resetToolBarsCActions(addedToolBarActions);
        }

        @Override
        public boolean removeAction(Action action) {
                removeActions(Arrays.asList(new Action[]{action}));
                return addedToolBarActions.contains(action);
        }

        @Override
        public void removeActions(List<Action> actionList) {
                addedToolBarActions.removeAll(actionList);
                resetToolBarsCActions(addedToolBarActions);
        }
        @Override
        public String addToolbarItem(Action action) {
                addActions(Arrays.asList(new Action[]{action}));
                return ActionTools.getMenuId(action);
        }

        private String addToolbarItem(CAction cAction,Action action, CLocation defaultLocation) {
                String id = ActionTools.getMenuId(action);
                if(id==null || commonControl.getSingleDockable(id)!=null) {
                        // Create a unique ID
                        int inc=1;
                        id = "action-"+inc;
                        while(commonControl.getSingleDockable(id)!=null) {
                                inc++;
                        }
                        String oldId="";
                        if(ActionTools.getMenuId(action)!=null) {
                                oldId = ActionTools.getMenuId(action);
                        }
                        LOGGER.warn(I18N.tr("ToolBar item {0} is not unique, it has been renamed to {1}",oldId,id));
                        action.putValue(ActionTools.MENU_ID,id);
                }
                ToolBarItem toolbar = new ToolBarItem(id,cAction,action);
                commonControl.addDockable(toolbar);
                try {
                        setLocation(toolbar,defaultLocation);
                } catch (RuntimeException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }
                toolbar.setVisible(true);
                return id;
        }

        /**
         * Find the most appropriate Location by reading group and insert instruction in Action properties.
         * @param toolbar New ToolBarItem, must be already registered in the CControl
         */
        private void setLocation(ToolBarItem toolbar, CLocation defaultLocation) {
                Action action = toolbar.getAction();
                // Default location is north
                toolbar.setLocation(defaultLocation);
                // Read actions properties
                String actionId = ActionTools.getMenuId(action);
                String insertAfter = ActionTools.getInsertAfterMenuId(action);
                String insertBefore = ActionTools.getInsertBeforeMenuId(action);
                String logicalGroup = ActionTools.getLogicalGroup(action);
                // Read other toolbars properties
                for(ToolBarItem toolBarItem : getToolBarItems()) {
                        // If this is not the same object instance
                        if(toolBarItem!=toolbar) {
                                Action activeAction = toolBarItem.getAction();
                                String activeId = ActionTools.getMenuId(activeAction);
                                String activeLogicalGroup = ActionTools.getLogicalGroup(activeAction);
                                String activeInsertAfter = ActionTools.getInsertAfterMenuId(activeAction);
                                String activeInsertBefore = ActionTools.getInsertBeforeMenuId(activeAction);
                                if((!insertAfter.isEmpty() && insertAfter.equals(activeId)) ||
                                        (!logicalGroup.isEmpty() && activeLogicalGroup.equals(logicalGroup)) ||
                                        (!activeInsertBefore.isEmpty() && activeInsertBefore.equals(actionId))
                                        ) {
                                        toolbar.setLocationsAside(toolBarItem);
                                        break;
                                } else if((!insertBefore.isEmpty() && insertBefore.equals(activeId)) ||
                                        (!activeInsertAfter.isEmpty() && activeInsertAfter.equals(actionId))) {
                                        toolbar.setLocationsAside(toolBarItem);
                                        toolBarItem.setLocationsAside(toolbar);
                                        break;
                                }
                        }
                }
        }
        @Override
        public boolean removeToolbarItem(Action action) {
                String id = ActionTools.getMenuId(action);
                if(id!=null) {
                        return commonControl.removeSingleDockable(id);
                }
                return false;
        }

        /**
         * Recreate all CAction and put them in already shown ToolBarItems.
         * Remove toolbars
         * @param actions
         */
        private void resetToolBarsCActions(List<Action> actions) {
            // Create root CAction, the size of rootActions might be smaller than actions
            ToolBarActions toolBarCActions = new ToolBarActions();
            toolBarCActions.setActions(actions);
            List<CAction> rootActions = toolBarCActions.getCustomActions();
            // Create Map of newly generated CActions in order to optimize updates checks.
            // Key: Action Menu ID
            Map<String,CAction> actionMap = new HashMap<String, CAction>(rootActions.size());
            Set<String> generatedId = new HashSet(actionMap.keySet());
            for(CAction action : rootActions) {
                if(action instanceof CActionHolder) {
                    actionMap.put(ActionTools.getMenuId(((CActionHolder)action).getAction()),action);
                } else {
                    LOGGER.warn("Generated CAction must implements CActionHolder interface");
                }
            }
            // Update and remove toolbars
            for(ToolBarItem item : getToolBarItems()) {
                String shownRootMenuId = ActionTools.getMenuId(item.getAction());
                CAction newCAction = actionMap.get(shownRootMenuId);
                if(newCAction==null) {
                    // This ToolBarItem has to be remove
                    commonControl.removeSingleDockable(shownRootMenuId);
                } else {
                    // The ToolBarItem's CAction must be replaced by the new one
                    generatedId.remove(shownRootMenuId);
                    item.setItem(newCAction);
                }
            }
            // Add new toolbars
            for(String newActionId : generatedId) {
                CAction newCAction = actionMap.get(newActionId);
                Action action = ((CActionHolder)newCAction).getAction();
                // Default Location depends on
                String logicalGroup = ActionTools.getLogicalGroup(action);
                CLocation defaultLocation = lastToolBarLocation.get(logicalGroup);
                if(defaultLocation==null) {
                    defaultLocation = area.getNorthToolbar().getStationLocation().group(lastToolBarLocation.size()).toolbar(0,0).item(0);
                    lastToolBarLocation.put(logicalGroup,defaultLocation);
                }
                addToolbarItem(newCAction,action,defaultLocation);
            }
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
                        } else if(dockable instanceof ToolBarItem) {
                                // Known dockable
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

        private static class ToolBarBackGround implements BackgroundPaint {
                @Override
                public void install(BackgroundComponent backgroundComponent) {
                }

                @Override
                public void uninstall(BackgroundComponent backgroundComponent) {
                        //ignore
                }

                @Override
                public void paint(BackgroundComponent backgroundComponent, PaintableComponent paintable, Graphics g) {
                        paintable.paintBackground( null );
                        g.setColor( UIManager.getColor("Panel.background") );
                        int w = paintable.getComponent().getWidth();
                        int h = paintable.getComponent().getHeight();
                        g.fillRect(0, 0, w, h);
                }
        }
}
