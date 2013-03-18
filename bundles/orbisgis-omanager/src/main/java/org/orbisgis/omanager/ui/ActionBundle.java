/*
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

package org.orbisgis.omanager.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * Root class of all bundle actions.
 * @author Nicolas Fortin
 */
public class ActionBundle extends AbstractAction {
    protected static Logger LOGGER = Logger.getLogger(ActionBundle.class);
    private ActionListener action;

    public ActionBundle(String label, String toolTipText,Icon icon) {
        super(label);
        putValue(SHORT_DESCRIPTION,toolTipText);
        putValue(SMALL_ICON, icon);
    }

    /**
     * Call this listener when the command is executed.
     * @param action
     * @return this
     */
    public ActionBundle setActionListener(ActionListener action) {
        this.action = action;
        return this;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        setEnabled(false);
        (new BackgroundProcess(actionEvent)).execute();
    }

    /**
     * Process bundle actions in background.
     */
    private class BackgroundProcess extends SwingWorker<Integer,Integer> {
        private ActionEvent actionEvent;

        private BackgroundProcess(ActionEvent actionEvent) {
            this.actionEvent = actionEvent;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            if(action!=null) {
                try {
                    action.actionPerformed(actionEvent);
                } catch( Exception ex) {
                    LOGGER.error(ex.getLocalizedMessage(),ex);
                }
            }
            return null;
        }


        @Override
        public String toString() {
            return "ActionBundle#BackgroundProcess";
        }

        @Override
        protected void done() {
            setEnabled(true);
        }
    }
}
