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
package org.orbisgis.view.components.actions;

import org.orbisgis.view.docking.DockingPanelParameters;

import javax.swing.*;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This property listen propagate the update of ActionCommands actions to DockingPanelParameters.
 * @author Nicolas Fortin
 */
public class ActionDockingListener implements PropertyChangeListener {
        private DockingPanelParameters dockingPanelParameters;
        private AtomicBoolean awaitingRunnable = new AtomicBoolean(false);

        /**
         * Constructor
         * @param dockingPanelParameters Parameters to update.
         */
        public ActionDockingListener(DockingPanelParameters dockingPanelParameters) {
            this.dockingPanelParameters = dockingPanelParameters;
        }


        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if(propertyChangeEvent.getPropertyName()!=null && propertyChangeEvent.getPropertyName().equals(ActionCommands.PROP_ACTIONS)) {
                if(propertyChangeEvent instanceof IndexedPropertyChangeEvent) {
                    // Update a single Action
                    if(!awaitingRunnable.getAndSet(true)) {
                        SwingUtilities.invokeLater(new CopyActionsProcess((ActionCommands)propertyChangeEvent.getSource()));
                    }
                } else {
                    // A new set of Action
                    if(!awaitingRunnable.getAndSet(true)) {
                        try {
                            dockingPanelParameters.setDockActions(((ActionCommands)propertyChangeEvent.getSource()).getActions());
                        } finally {
                            awaitingRunnable.set(false);
                        }
                    }
                }
            }
        }

        private class CopyActionsProcess implements Runnable {
            ActionCommands commands;

            private CopyActionsProcess(ActionCommands commands) {
                this.commands = commands;
            }

            @Override
            public void run() {
                List<Action> actions = new ArrayList<Action>(commands.getActions());
                awaitingRunnable.set(false);
                dockingPanelParameters.setDockActions(actions);
            }
        }
}
