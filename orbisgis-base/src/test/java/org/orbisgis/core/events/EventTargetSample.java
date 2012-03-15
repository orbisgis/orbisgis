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
package org.orbisgis.core.events;

/**
 * Event target sample, the target is an object that catch an Event and process it.
 * The target doesn't have to include the Source class.
 * The target can be the model, not dependencies with source or controler.
 */
public class EventTargetSample {
    private boolean firedRootEvent = false;
    private boolean firedSubEvent = false;
    private String privateMessage = "";
    /**
     * 
     * @return The message passed from the source event
     */
    public String getPrivateMessage() {
        return privateMessage;
    }
    /**
     * 
     * @param privateMessage The message to set, called by the listener
     */
    public void setPrivateMessage(String privateMessage) {
        this.privateMessage = privateMessage;
    }
    /**
     * Is the listener call setFireRootEvent()
     * @return 
     */
    public boolean isFiredRootEvent() {
        return firedRootEvent;
    }
    /**
     * This method is linked with the listener
     */
    public void setFiredRootEvent() {
        this.firedRootEvent = true;
    }
    /**
     * 
     * @return True if setFiredRootEvent has been called
     */
    public boolean isFiredSubEvent() {
        return firedSubEvent;
    }

    /**
     * This method is linked with the listener
     */
    public void setFiredSubEvent() {
        this.firedSubEvent = true;
    }

}
