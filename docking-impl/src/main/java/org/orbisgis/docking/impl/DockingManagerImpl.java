/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.docking.impl;

import bibliothek.extension.gui.dock.preference.PreferenceTreeDialog;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.gui.dock.ToolbarDockStation;
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
import bibliothek.gui.dock.util.*;
import bibliothek.util.PathCombiner;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.orbisgis.commons.events.BeanPropertyChangeSupport;
import org.orbisgis.docking.icons.DockingIcon;
import org.orbisgis.docking.impl.internals.ApplicationRessourceDecorator;
import org.orbisgis.docking.impl.internals.CustomMultipleCDockable;
import org.orbisgis.docking.impl.internals.CustomPanelHolder;
import org.orbisgis.docking.impl.internals.CustomSingleCDockable;
import org.orbisgis.docking.impl.internals.DockingArea;
import org.orbisgis.docking.impl.internals.DockingPanelLayoutDecorator;
import org.orbisgis.docking.impl.internals.InternalCommonFactory;
import org.orbisgis.docking.impl.internals.OrbisGISView;
import org.orbisgis.docking.impl.internals.actions.CActionHolder;
import org.orbisgis.docking.impl.internals.actions.ToolBarActions;
import org.orbisgis.docking.impl.internals.actions.ToolBarItem;
import org.orbisgis.docking.impl.preferences.OrbisGISPreferenceTreeModel;
import org.orbisgis.docking.impl.preferences.editors.UserInformationEditor;
import org.orbisgis.mainframe.api.MainWindow;
import org.orbisgis.sif.components.actions.ActionFactoryService;
import org.orbisgis.sif.components.actions.ActionsHolder;
import org.orbisgis.sif.components.actions.MenuTrackerAction;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelFactory;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.viewapi.components.actions.ActionTools;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
/**
 * Initialize and manage CControl.
 *
 * This manager can save and load emplacement of views in XML.
 */
@Component(service = DockingManager.class)
public final class DockingManagerImpl extends BeanPropertyChangeSupport implements DockingManager, ActionsHolder {

    private JFrame owner;
    private SingleCDockableListMenuPiece dockableMenuTracker;
    private static final I18n I18N = I18nFactory.getI18n(DockingManagerImpl.class);
    private static final Logger LOGGER = LoggerFactory.getLogger("gui." + DockingManagerImpl.class);
    private File dockingState = null;
    private static final boolean DOCKING_STATE_XML = true;
    private CControl commonControl; /*!< link to the docking-frames */
    //Docking Area (DockingFrames feature named WorkingArea)
    private Map<String, DockingArea> dockingAreas = new HashMap<>();
    /**
     * the available preferences for docking frames
     */
    private PreferenceTreeModel preferences;
    private CToolbarContentArea area;
    // In order to separate toolbars keep unique count
    private int createdToolbars = 0;
    // Action provided to this DockingManager
    private List<Action> addedToolBarActions = new LinkedList<Action>();
    private Map<ActionFactoryService, MenuTrackerAction> actionFromFactory = new HashMap<>();

    /**
     * Creates the new manager
     *
     * @param owner the window used as parent for all dialogs
     */
    public DockingManagerImpl(JFrame owner) {
    }

    @Reference
    public void setMainWindow(MainWindow mainWindow) {
        this.owner = mainWindow.getMainFrame();
        // Method bibliothek.gui.dock.util.DockUtilities.checkLayoutLocked(DockUtilities.java:723)
        // Throw a RuntimeException: java.lang.Error: Trampoline must not be defined by the bootstrap classloader
        DockUtilities.disableCheckLayoutLocked();
        commonControl = new CControl(owner);
        commonControl.addControlListener(new DockingListener());
        dockableMenuTracker = new SingleCDockableListMenuPiece(commonControl);
        //Retrieve the Docking Frames Preferences
        preferences = new OrbisGISPreferenceTreeModel(commonControl, PathCombiner.APPEND);
        commonControl.setPreferenceModel(preferences);

        //DEFAULT property of a view
        // commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, I18N.tr("Docked
        // Window") );
        commonControl.getController().getProperties().set(PropertyKey.DOCK_STATION_ICON, DockingIcon.getIcon
                ("orbisgis"));
        commonControl.getController().getThemeManager().setBackgroundPaint(ThemeManager.BACKGROUND_PAINT + ".station" +
                ".toolbar.container", new ToolBarBackGround());
        commonControl.putProperty(ToolbarDockStation.SIDE_GAP, 2);
        commonControl.putProperty(ToolbarDockStation.GAP, 2);

        //StackDockStation will contain all instances of ReservedDockStation
        area = new CToolbarContentArea(commonControl, "base");
        commonControl.addStationContainer(area);
        owner.add(area);
    }

    public void unsetMainWindow(MainWindow mainWindow) {

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
        if (SwingUtilities.isEventDispatchThread()) {
            commonControl.removeSingleDockable(dockId);
        } else {
            RemovePanel removePanel = new RemovePanel(commonControl, dockId);
            try {
                SwingUtilities.invokeAndWait(removePanel);
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     * Used by declarative services
     * @param dockingPanel Panel instance to remove
     */
    public void removeDockingPanel(DockingPanel dockingPanel) {
        if (SwingUtilities.isEventDispatchThread()) {
            commonControl.removeSingleDockable(dockingPanel.getDockingParameters().getName());
        } else {
            RemovePanel removePanel = new RemovePanel(commonControl, dockingPanel.getDockingParameters().getName());
            try {
                SwingUtilities.invokeAndWait(removePanel);
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     * @return The look and feel menu
     */
    @Override
    public JMenu getLookAndFeelMenu() {
        RootMenuPiece laf = new RootMenuPiece(I18N.tr("&Look And Feel"), false, new CLookAndFeelMenuPiece
                (commonControl));
        JMenu menu = laf.getMenu();
        MenuCommonFunctions.setMnemonic(menu);
        return menu;
    }

    /**
     * @return The menu that shows items declared in the docking
     */
    @Override
    public JMenu getCloseableDockableMenu() {
        RootMenuPiece windows = new RootMenuPiece(I18N.tr("&Windows"), false, dockableMenuTracker);
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
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(dockingState));
            XElement element = XIO.readUTF(in);
            in.close();
            // Use the docking frame serialisation
            commonControl.readXML(element);
            // Use OrbisGIS serialisation
            for (CustomPanelHolder panel : getPanelDecorator()) {
                // Only SingleCDockable is not managed for custom layout information
                if (panel instanceof CustomSingleCDockable) {
                    CustomSingleCDockable scdockable = (CustomSingleCDockable) panel;
                    DockingPanelLayout layout = scdockable.getDockingPanel().getDockingParameters().getLayout();
                    if (layout != null) {
                        commonControl.getResources().put(scdockable.getUniqueId(), new ApplicationRessourceDecorator
                                (new DockingPanelLayoutDecorator(layout)));
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
        // When reading a layout file, all components that are not in the layout file are hidden by DockingFrames
        // Some components cannot be hidden or shown by the user, the following lines
        // restore the visibility these components.
        // Check that non closable frame are shown
        for (DockingPanel panel : getPanels()) {
            DockingPanelParameters params = panel.getDockingParameters();
            if (!params.isCloseable() && !params.isVisible()) {
                params.setVisible(true);
            }
        }
        // Check that all non empty toolbars are visible
        // Empty hidden toolbars are kept in order to restore/save the layout.
        boolean doReset = false;
        for (ToolBarItem item : getToolBarItems()) {
            if (!item.isVisible() && item.getAction() != null) {
                doReset = true;
                // Reset layout
                // Unlink action and removed ToolBar item
                item.resetItem();
                commonControl.removeSingleDockable(item.getUniqueId());
            }
        }
        if (doReset) {
            resetToolBarsCActions(addedToolBarActions);
        } else {
            // All toolbars have been set to visible in order to set layout
            // The visible state can be reset here
            // Set the visibility of all ToolBarItems
            refreshToolBarsState();
        }
    }

    private void writeXML() throws IOException {
        // Not visible toolbars cannot retrieve their state on next OrbisGIS loading
        for (ToolBarItem item : getToolBarItems()) {
            item.setVisible(true);
            item.setTrackActionVisibleState(false);
        }
        // Make an empty XML tree
        XElement root = new XElement("root");

        // Use the docking frame serialisation
        commonControl.writeXML(root);
        // Save the tree in the file
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dockingState));
        XIO.writeUTF(root, out);
        out.close();
        // Recover original state
        refreshToolBarsState();
    }

    /**
     * Apply the ToolBar action visible state to their ToolBarItems
     */
    private void refreshToolBarsState() {
        for (ToolBarItem item : getToolBarItems()) {
            Action itemAction = item.getAction();
            if (itemAction != null) {
                item.setVisible(ActionTools.isVisible(itemAction));
            } else {
                item.setVisible(false);
            }
            item.setTrackActionVisibleState(true);
        }
    }

    /**
     * Load the docking layout
     */
    private void loadLayout() {
        if (dockingState != null) {
            if (dockingState.exists()) {
                if (DOCKING_STATE_XML) {
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
        if (dockingState != null) {
            try {
                if (DOCKING_STATE_XML) {
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
        PreferenceTreeDialog dialog = new PreferenceTreeDialog(preferences, true);
        //Add custom editors
        dialog.setEditorFactory(UserInformationEditor.TYPE_USER_INFO, UserInformationEditor.FACTORY);
        //Show dialog
        dialog.openDialog(owner, true);
    }

    @Override
    public void registerPanelFactory(String factoryName, DockingPanelFactory factory) {
        InternalCommonFactory dockingFramesFactory = new InternalCommonFactory(factory, commonControl);
        commonControl.addMultipleDockableFactory(factoryName, dockingFramesFactory);
    }

    @Override
    public void unregisterPanelFactory(String factoryName) {
        commonControl.removeMultipleDockableFactory(factoryName);
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
     *
     * @param panel
     * @return DefaultCDockable instance, null if not exists
     */
    public CDockable getDockable(DockingPanel panel) {
        int count = commonControl.getCDockableCount();
        for (int i = 0; i < count; i++) {
            CDockable libComponent = commonControl.getCDockable(i);
            if (libComponent instanceof CustomPanelHolder) {
                DockingPanel cPanel = ((CustomPanelHolder) libComponent).getDockingPanel();
                if (cPanel.equals(panel)) {
                    return libComponent;
                }
            }
        }
        return null;
    }

    /**
     * Get the intermediate panels
     *
     * @return
     */
    private List<CustomPanelHolder> getPanelDecorator() {
        List<CustomPanelHolder> activePanel = new LinkedList<CustomPanelHolder>();
        int count = commonControl.getCDockableCount();
        for (int i = 0; i < count; i++) {
            CDockable dockable = commonControl.getCDockable(i);
            if (dockable instanceof CustomPanelHolder) {
                activePanel.add(((CustomPanelHolder) dockable));
            }
        }
        return activePanel;
    }

    private List<ToolBarItem> getToolBarItems() {
        List<ToolBarItem> toolBarItemList = new LinkedList<ToolBarItem>();
        int count = commonControl.getCDockableCount();
        for (int i = 0; i < count; i++) {
            CDockable dockable = commonControl.getCDockable(i);
            if (dockable instanceof ToolBarItem) {
                toolBarItemList.add((ToolBarItem) dockable);
            }
        }
        return toolBarItemList;
    }

    /**
     * Get the current opened panels
     *
     * @return
     */
    @Override
    public List<DockingPanel> getPanels() {
        List<DockingPanel> activePanel = new ArrayList<DockingPanel>();
        for (CustomPanelHolder holder : getPanelDecorator()) {
            activePanel.add(holder.getDockingPanel());
        }
        return activePanel;
    }


    /**
     * DockingManagerImpl will load and save the panels layout
     * in the specified file. Load the layout if the file exists.
     *
     * @param dockingStateFilePath Destination of the default persistence file
     */
    @Override
    public void setDockingLayoutPersistanceFilePath(String dockingStateFilePath) {
        this.dockingState = new File(dockingStateFilePath);
        loadLayout();
    }

    /**
     * Create a new dockable corresponding to this layout
     *
     * @param factoryId   The factory id registerPanelFactory:factoryName
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
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public String addDockingPanel(DockingPanel frame) {
        if (SwingUtilities.isEventDispatchThread()) {
            return show(frame);
        } else {
            AddPanel addPanel = new AddPanel(this, frame);
            try {
                SwingUtilities.invokeAndWait(addPanel);
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                return null;
            }
            return addPanel.getPanelId();
        }
    }

    /**
     * Shows a view at the given location as child
     * of <code>root</code>.
     *
     * @param frame the <code>DockingPanel</code> for which a view should be opened
     * @return Dockable unique ID
     */
    private String show(DockingPanel frame) {
        //Create the DockingFrame item
        if (frame.getDockingParameters().getName().isEmpty()) {
            //If the dev doesn't define a name on the panel
            //We set the name as the name of the class
            frame.getDockingParameters().setName(frame.getClass().getCanonicalName());
        }
        SingleCDockable dockItem = OrbisGISView.createSingle(frame, commonControl);
        //Place the item in a dockstation
        String restrictedAreaName = frame.getDockingParameters().getDockingArea();
        if (!restrictedAreaName.isEmpty()) {
            //This item is restricted to an area
            DockingArea dockArea = dockingAreas.get(restrictedAreaName);
            if (dockArea == null) {
                dockArea = new DockingArea(commonControl.createWorkingArea(restrictedAreaName));
                dockArea.getWorkingArea().setVisible(true);
                dockingAreas.put(restrictedAreaName, dockArea);
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
        List<Action> before = new ArrayList<Action>(addedToolBarActions);
        addedToolBarActions.addAll(newActions);
        resetToolBarsCActions(addedToolBarActions);
        propertyChangeSupport.firePropertyChange(PROP_ACTIONS, before, addedToolBarActions);
    }

    @Override
    public boolean removeAction(Action action) {
        removeActions(Arrays.asList(action));
        return addedToolBarActions.contains(action);
    }

    @Override
    public void removeActions(List<Action> actionList) {
        List<Action> before = new ArrayList<Action>(addedToolBarActions);
        addedToolBarActions.removeAll(actionList);
        resetToolBarsCActions(addedToolBarActions);
        propertyChangeSupport.firePropertyChange(PROP_ACTIONS, before, addedToolBarActions);
    }

    @Override
    public String addToolbarItem(Action action) {
        addActions(Arrays.asList(action));
        return ActionTools.getMenuId(action);
    }

    private ToolBarItem addToolbarItem(CAction cAction, Action action, CLocation defaultLocation) {
        String id = ActionTools.getMenuId(action);
        if (id == null || commonControl.getSingleDockable(id) != null) {
            // Create a unique ID
            int inc = 1;
            id = "action-" + inc;
            while (commonControl.getSingleDockable(id) != null) {
                inc++;
            }
            String oldId = "";
            if (ActionTools.getMenuId(action) != null) {
                oldId = ActionTools.getMenuId(action);
            }
            LOGGER.warn(I18N.tr("ToolBar item {0} is not unique, it has been renamed to {1}", oldId, id));
            action.putValue(ActionTools.MENU_ID, id);
        }
        ToolBarItem toolbar = new ToolBarItem(id, cAction);
        commonControl.addDockable(toolbar);
        try {
            setLocation(toolbar, defaultLocation);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        return toolbar;
    }

    /**
     * Find the most appropriate Location by reading group and insert instruction in Action properties.
     *
     * @param toolbar New ToolBarItem, must be already registered in the CControl
     */
    private void setLocation(ToolBarItem toolbar, CLocation defaultLocation) {
        Action action = toolbar.getAction();
        // Default location is north
        boolean locationSet = false;
        // Read actions properties
        String actionId = ActionTools.getMenuId(action);
        String insertAfter = ActionTools.getInsertAfterMenuId(action);
        String insertBefore = ActionTools.getInsertBeforeMenuId(action);
        String logicalGroup = ActionTools.getLogicalGroup(action);
        // Read other toolbars properties
        // Find specified placement by item key
        for (ToolBarItem toolBarItem : getToolBarItems()) {
            // If this is not the same object instance
            if (toolBarItem != toolbar) {
                Action activeAction = toolBarItem.getAction();
                String activeId = ActionTools.getMenuId(activeAction);
                String activeInsertAfter = ActionTools.getInsertAfterMenuId(activeAction);
                String activeInsertBefore = ActionTools.getInsertBeforeMenuId(activeAction);
                if ((!insertAfter.isEmpty() && insertAfter.equals(activeId)) || (!activeInsertBefore.isEmpty() &&
                        activeInsertBefore.equals(actionId))) {
                    setNextPosition(toolBarItem, toolbar);
                    locationSet = true;
                    break;
                } else if ((!insertBefore.isEmpty() && insertBefore.equals(activeId)) || (!activeInsertAfter.isEmpty
                        () && activeInsertAfter.equals(actionId))) {
                    setNextPosition(toolBarItem, toolbar);
                    setNextPosition(toolbar, toolBarItem);
                    locationSet = true;
                    break;
                }
            }
        }
        // If not found use the logical group
        if (!locationSet && !logicalGroup.isEmpty()) {
            ToolBarItem lastGroupItem = null;
            for (ToolBarItem toolBarItem : getToolBarItems()) {
                // If this is not the same object instance
                if (toolBarItem != toolbar) {
                    Action activeAction = toolBarItem.getAction();
                    String activeLogicalGroup = ActionTools.getLogicalGroup(activeAction);
                    if ((!logicalGroup.isEmpty() && activeLogicalGroup.equals(logicalGroup))) {
                        lastGroupItem = toolBarItem;
                    }
                }
            }
            if (lastGroupItem != null) {
                setNextPosition(lastGroupItem, toolbar);
                locationSet = true;
            }
        }
        if (!locationSet) {
            toolbar.setLocation(defaultLocation);
            toolbar.setVisible(true);
        }
    }

    @Override
    public boolean removeToolbarItem(Action action) {
        String id = ActionTools.getMenuId(action);
        return id != null && commonControl.removeSingleDockable(id);
    }

    private void setNextPosition(ToolBarItem item, ToolBarItem itemNext) {
        if (!item.isVisible()) {
            item.setVisible(true);
        }
        // itemNext.setLocationsAside(item);
        CLocation location = commonControl.getLocationManager().getLocation(item.intern());
        if (location != null) {
            itemNext.setLocation(location.aside());
            itemNext.setVisible(true);
        }
    }

    /**
     * Recreate all CAction and put them in already shown ToolBarItems.
     * Remove toolbars that hold action not provided and add toolbars.
     *
     * @param actions Actions to show in toolbars
     */
    private void resetToolBarsCActions(List<Action> actions) {
        // Key: Logical Value: Last set location
        Map<String, CLocation> lastToolBarLocation = new HashMap<String, CLocation>();
        // Create root CAction, the size of rootActions might be smaller than actions
        ToolBarActions toolBarCActions = new ToolBarActions();
        toolBarCActions.setActions(actions);
        List<CAction> rootActions = toolBarCActions.getCustomActions();
        // Create Map of newly generated CActions in order to optimize updates checks.
        // Key: Action Menu ID
        Map<String, CAction> actionMap = new HashMap<String, CAction>(rootActions.size());
        Set<String> generatedId = new LinkedHashSet<String>();
        for (CAction action : rootActions) {
            if (action instanceof CActionHolder) {
                String key = ActionTools.getMenuId(((CActionHolder) action).getAction());
                actionMap.put(key, action);
                generatedId.add(key);
            } else {
                // Ignore Menu Separator
            }
        }
        // Update and remove toolbars
        for (ToolBarItem item : getToolBarItems()) {
            String shownRootMenuId = item.getUniqueId();
            CAction newCAction = actionMap.get(shownRootMenuId);
            if (newCAction == null) {
                // This ToolBarItem has to be reset
                item.resetItem();
            } else {
                // The ToolBarItem's CAction must be replaced by the new one
                generatedId.remove(shownRootMenuId);
                item.setItem(newCAction);
                if (!item.isVisible()) {
                    item.setVisible(true);
                }
            }
        }
        for (String newActionId : generatedId) {
            CAction newCAction = actionMap.get(newActionId);
            Action action = ((CActionHolder) newCAction).getAction();
            // Default Location depends on
            String logicalGroup = ActionTools.getLogicalGroup(action);
            CLocation defaultLocation = getDefaultLocation(lastToolBarLocation, logicalGroup);
            lastToolBarLocation.put(logicalGroup, defaultLocation);
            addToolbarItem(newCAction, action, defaultLocation);
        }
        refreshToolBarsState();
    }

    @SuppressWarnings("deprecation")
    private CLocation getDefaultLocation(Map<String, CLocation> lastToolBarLocation, String logicalGroupId) {
        CLocation defaultLocation = lastToolBarLocation.get(logicalGroupId);
        if (defaultLocation == null) {
            defaultLocation = area.getNorthToolbar().getStationLocation().group(createdToolbars++).toolbar(0, 0).item
                    (0);
        } else {
            // Aside is deprecated but It has to be used if the ToolBarItem is new.
            defaultLocation = defaultLocation.aside();
        }
        return defaultLocation;
    }

    @Override
    public <TargetComponent> void addActionFactory(ActionFactoryService<TargetComponent> factory, TargetComponent
            targetComponent) throws IllegalArgumentException {
        if (!actionFromFactory.containsKey(factory)) {
            MenuTrackerAction<TargetComponent> menuTrackerAction = new MenuTrackerAction<>(factory, factory
                    .createActions(targetComponent), targetComponent);
            addActions(menuTrackerAction.getActions());
            actionFromFactory.put(factory, menuTrackerAction);
        } else {
            // Developer exception
            throw new IllegalArgumentException("ActionFactoryService instance is already pushed");
        }
    }

    @Override
    public <TargetComponent> void removeActionFactory(ActionFactoryService<TargetComponent> actionFactoryService) {
        MenuTrackerAction<TargetComponent> trackerAction = actionFromFactory.get(actionFactoryService);
        if (trackerAction != null) {
            removeActions(trackerAction.getActions());
            actionFactoryService.disposeActions(trackerAction.getTargetComponent(), trackerAction.getActions());
            actionFromFactory.remove(actionFactoryService);
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
            if (dockable instanceof CustomPanelHolder && dockable instanceof DefaultCDockable) {
                CustomPanelHolder dockItem = (CustomPanelHolder) dockable;
                OrbisGISView.setListeners(dockItem.getDockingPanel(), (DefaultCDockable) dockable);
            } else if (!(dockable instanceof ToolBarItem)) {
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
            paintable.paintBackground(null);
            g.setColor(UIManager.getColor("Panel.background"));
            int w = paintable.getComponent().getWidth();
            int h = paintable.getComponent().getHeight();
            g.fillRect(0, 0, w, h);
        }
    }

    private static class AddPanel implements Runnable {
        private final DockingManagerImpl dockingManager;
        private final DockingPanel dockingPanel;
        private String panelId;

        private AddPanel(DockingManagerImpl dockingManager, DockingPanel dockingPanel) {
            this.dockingManager = dockingManager;
            this.dockingPanel = dockingPanel;
        }

        @Override
        public void run() {
            panelId = dockingManager.show(dockingPanel);
        }

        public String getPanelId() {
            return panelId;
        }
    }

    private static class RemovePanel implements Runnable {
        private final CControl control;
        private final String panelId;

        private RemovePanel(CControl control, String panelId) {
            this.control = control;
            this.panelId = panelId;
        }

        @Override
        public void run() {
            control.removeSingleDockable(panelId);
        }
    }
}
