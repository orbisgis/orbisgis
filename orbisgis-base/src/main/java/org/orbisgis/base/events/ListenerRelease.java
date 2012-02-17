/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
 *null
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
/**
 * @package org.orbisgis.view.events
 * @brief Helper to quickly release Listeners from their target
 */
package org.orbisgis.base.events;

import org.orbisgis.base.events.internals.ListenerContainers;
/**
 * @brief Release all listeners attached to a specific target in one call.
 */
public class ListenerRelease {

    private ListenerContainers containers = new ListenerContainers();

    /**
     * Add a container to manage with this class
     * @param container The container instance
     */
    public void addContainer(ListenerContainer container) {
        containers.add(container);
    }
    /**
     * When a target is no longer used, the listeners created by it must be removed.
     */
    public void releaseListeners(Object target) {
        for(ListenerContainer container : containers) {
                container.removeListeners(target);
        }
    }
    /**
     * Remove all listeners of all containers.
     */
    public void clearListeners() {
        for(ListenerContainer container : containers) {
                container.clearListeners();
        }        
    }
}
