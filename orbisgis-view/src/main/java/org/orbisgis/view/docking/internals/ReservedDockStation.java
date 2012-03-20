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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import org.orbisgis.view.docking.DockingAreaParameters;

/**
 * This DockStation accept only OrbisGISView with
 */
public class ReservedDockStation extends SplitDockStation {
    private DockingAreaParameters dockingAreaParameters = new DockingAreaParameters();
    private String dockingAreaName;
    /**
     * ReservedDockStation constructor
     * @param name The name of the DockingArea
     */    
    public ReservedDockStation(String dockingAreaName) {
        this.dockingAreaName = dockingAreaName;
        setPropertyListeners();
    }
    
    /**
     * 
     * @return The docking area name, used by OrbisGISView to accept or not this
     * docking area.
     */
    public String getDockingAreaName() {
        return dockingAreaName;
    }
    /**
     * @return The parameters of docking area
     */
    public DockingAreaParameters getDockingAreaParameters() {
        return dockingAreaParameters;
    }

    /**
     * Tells whether this station accepts <code>child</code> as a new child,
     * or refuses <code>child</code>. The user will not be able to drop
     * a {@link Dockable} onto this station if this method returns
     * <code>false</code>.
     * @param child a {@link Dockable} which may become a child
     * @return <code>true</code> if <code>child</code> is accepted
     */
    @Override
    public boolean accept( Dockable child ) {
        //Accept only child dedicated to this docking station name
        if(child instanceof OrbisGISView) {
            String childDockingAreaName = ((OrbisGISView) child).getDockableParameters().getDockingArea();
            return childDockingAreaName.equals(dockingAreaName);
        }else{
            return false;
        }
    }  
    
    
    
    /**
     * Link DefaultDockable parameters with OrbisGis parameters
     */
    private void setPropertyListeners() {
        dockingAreaParameters.addPropertyChangeListener(
                DockingAreaParameters.PROP_AREATITLE,
                EventHandler.create(PropertyChangeListener.class,
                                    this,
                                    "setTitleText",
                                    "newValue"
                ));
        dockingAreaParameters.addPropertyChangeListener(
                DockingAreaParameters.PROP_AREAICON,
                EventHandler.create(PropertyChangeListener.class,
                                    this,
                                    "setTitleIcon",
                                    "newValue"
                ));
    }
}
