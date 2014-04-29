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
package org.orbisgis.view.docking.internals;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CSeparator;
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.*;

import org.orbisgis.viewapi.docking.DockingLocation;
import org.orbisgis.viewapi.docking.DockingPanel;
import org.orbisgis.viewapi.docking.DockingPanelParameters;
import org.orbisgis.view.docking.internals.actions.ToolBarActions;

/**
 * This is the link between the DockingPanel and DockingFrames
 * 
 * Listen to DockingPanelParameters to change the behaviour of this dockable.
 * 
 * This class help to add OrbisGis specific actions ( Reduce, close view ..)
 * and custom components like custom titles.
 */
public class OrbisGISView {
    private DefaultCDockable internalDock;
    private DockingPanelParameters dockableParameters;
    private ToolBarActions customActions = new ToolBarActions();
    /**
     * Constructor of the OrbisGISView
     * @param dockingPanel The dockingPanel instance
     */
    private OrbisGISView(DockingPanel dockingPanel,DefaultCDockable internalDock) {
        this.dockableParameters = dockingPanel.getDockingParameters();
        this.internalDock = internalDock;
    }
    /**
     * Create a DockingFrame single dockable from the DockingPanel instance
     * @param dockingPanel
     * @param ccontrol
     * @return 
     */
    public static SingleCDockable createSingle(DockingPanel dockingPanel, CControl ccontrol) {
        CustomSingleCDockable dockItem = new CustomSingleCDockable(dockingPanel,dockingPanel.getDockingParameters().getName(),dockingPanel.getComponent());
        applyDefaultLocation(dockItem,dockingPanel,ccontrol);
        return dockItem;
    }
    /**
     * Create a DockingFrame multiple dockable from the DockingPanel
     * @param dockingPanel
     * @param factory
     * @param ccontrol
     * @return 
     */
    public static CustomMultipleCDockable createMultiple(DockingPanel dockingPanel,InternalCommonFactory factory, CControl ccontrol) {
        if(dockingPanel!=null) {
                CustomMultipleCDockable dockItem = new CustomMultipleCDockable(dockingPanel,factory);
                applyDefaultLocation(dockItem,dockingPanel,ccontrol);
                return dockItem;
        } else {
                return null;
        }
    }
    /**
     * Fetch the ccontrol panel list to find the provided panelName
     * @param ccontrol
     * @param panelName 
     * @return The panel location
     */
    private static CLocation getPanelLocation(CControl ccontrol, String panelName) {
            SingleCDockable dockable = ccontrol.getSingleDockable(panelName);
            if(dockable!=null) {
                    return dockable.getBaseLocation();
            }
            MultipleCDockable mDockable = ccontrol.getMultipleDockable(panelName);
            if(mDockable!=null) {
                    return mDockable.getBaseLocation();
            }
            return CLocation.base();
    }
    /**
     * Read the docking panel location preference and apply it in DockingFrame
     * @param dockItem
     * @param dockingPanel
     * @param ccontrol 
     */
    private static void applyDefaultLocation(AbstractCDockable dockItem,DockingPanel dockingPanel, CControl ccontrol) {
            DockingLocation dockingLocation = dockingPanel.getDockingParameters().getDefaultDockingLocation();
            CLocation referenceLocation = getPanelLocation(ccontrol, dockingLocation.getReferenceName());
            switch(dockingLocation.getPosition()) {
                    case STACKED_ON:
                            dockItem.setLocation(referenceLocation);
                            break;
            }
            
    }
    /**
     * read the OrbisGIS DockingPanelParameters of the panel
     * and apply to the DockingFrames panel instance
     * @param dockingPanel
     * @param dockItem 
     */
    public static void setListeners(DockingPanel dockingPanel,DefaultCDockable dockItem) {
        new OrbisGISView(dockingPanel,dockItem).init(dockItem);
    }
    
    
    /**
     * Add listeners 
     * @param internalDock 
     */
    private void init(DefaultCDockable internalDock) {
        internalDock.setTitleText(dockableParameters.getTitle());
        if(dockableParameters.getTitleIcon()!=null) {
            internalDock.setTitleIcon(dockableParameters.getTitleIcon());
        }
        setPropertyListeners();
        internalDock.setMinimizable(dockableParameters.isMinimizable());
        internalDock.setExternalizable(dockableParameters.isExternalizable());
        internalDock.setCloseable(dockableParameters.isCloseable());
        internalDock.setVisible(dockableParameters.isVisible());
        onSetActions(dockableParameters.getDockActions());
    }
    /**
     * Give access to the panel parameters
     * @return DockingPanelParameters instance
     */
    public DockingPanelParameters getDockableParameters() {
        return dockableParameters;
    }

    
    /**
     * Clear custom actions list
     */
    private void clearCustomActions() {
            for(CAction customAction : customActions.getCustomActions()) {
                internalDock.removeAction(customAction);
            }
    }
    /**
     * Copy CAction list into this View Action
     * @param actions 
     */
    private void copyActions(List<CAction> actions) {
        for(CAction action : actions) {
            internalDock.addAction(action);
        }
        // Add a final separator
        internalDock.addAction(CSeparator.SEPARATOR);
    }
    /**
     * The toolBar has been updated, translate JToolBar
     * into a ActionToolBar automatically
     * @param actions Actions to convert
     */
    public final void onSetActions(List<Action> actions) {
            clearCustomActions();
            customActions.setActions(actions);
            copyActions(customActions.getCustomActions());
    }
    
    /**
     * Link DefaultDockable parameters with OrbisGis parameters
     */
    private void setPropertyListeners() {
            
        //Link title text change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_TITLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                internalDock,
                                                "setTitleText",
                                                "newValue"));
        //Link title icon change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_TITLEICON,
                            EventHandler.create(PropertyChangeListener.class,
                                                internalDock,
                                                "setTitleIcon",
                                                "newValue"));
        //Link minimizable state change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_MINIMIZABLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                internalDock,
                                                "setMinimizable",
                                                "newValue"));
        //Link externalizable state change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_EXTERNALIZABLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                internalDock,
                                                "setExternalizable",
                                                "newValue"));
        //Link closeable state change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_CLOSEABLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                internalDock,
                                                "setCloseable",
                                                "newValue"));
        //Link Actions state change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_DOCK_ACTIONS,
                            EventHandler.create(PropertyChangeListener.class,
                                                this,
                                                "onSetActions",
                                                "newValue"));
        //Link visible state change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_VISIBLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                internalDock,
                                                "setVisible",
                                                "newValue"));
    }
}
