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

package org.orbisgis.view.docking.internals;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
/**
 * @brief The OrbisGis form of Docking Frames dockable
 * 
 * Listen to DockingPanelParameters to change the behaviour of this dockable.
 * 
 * This class help to add OrbisGis specific actions ( Reduce, close view ..)
 * and custom compenents like custom titles.
 */
public class OrbisGISView  extends DefaultSingleCDockable {
    private DockingPanelParameters dockableParameters;

    /**
     * Give access to the panel parameters
     * @return DockingPanelParameters instance
     */
    public DockingPanelParameters getDockableParameters() {
        return dockableParameters;
    }

    /**
     * Constructor of the OrbisGISView
     * @param dockingPanel The dockingPanel instance
     */
    public OrbisGISView(DockingPanel dockingPanel) {
        super(dockingPanel.getDockingParameters().getName(),dockingPanel.getComponent());
        this.dockableParameters = dockingPanel.getDockingParameters();
        this.setTitleText(dockableParameters.getTitle());
        if(dockableParameters.getTitleIcon()!=null) {
            this.setTitleIcon(dockableParameters.getTitleIcon());
        }
        setPropertyListeners();
        setMinimizable(dockableParameters.isMinimizable());
        setExternalizable(dockableParameters.isExternalizable());
    }
    /**
     * Link DefaultDockable parameters with OrbisGis parameters
     */
    private void setPropertyListeners() {
        //Link title text change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_TITLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                this,
                                                "setTitleText",
                                                "newValue"));
        //Link title icon change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_TITLEICON,
                            EventHandler.create(PropertyChangeListener.class,
                                                this,
                                                "setTitleIcon",
                                                "newValue"));
        //Link minimizable state change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_MINIMIZABLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                this,
                                                "setMinimizable",
                                                "newValue"));
        //Link externalizable state change
        dockableParameters.addPropertyChangeListener(
                            DockingPanelParameters.PROP_EXTERNALIZABLE,
                            EventHandler.create(PropertyChangeListener.class,
                                                this,
                                                "setExternalizable",
                                                "newValue"));
  
    }
    
}
