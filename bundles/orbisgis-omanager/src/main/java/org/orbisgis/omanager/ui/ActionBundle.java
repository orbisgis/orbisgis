/*
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

package org.orbisgis.omanager.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Root class of all bundle actions.
 * @author Nicolas Fortin
 */
public class ActionBundle extends AbstractAction {
     private static final I18n I18N = I18nFactory.getI18n(ActionBundle.class);
    protected static Logger LOGGER = Logger.getLogger(ActionBundle.class);
    private ActionListener action;
    private final boolean isPlugin;
    private final Component frame;

    public ActionBundle(String label, String toolTipText,Icon icon, Component frame, boolean isPlugin) {
        super(label);
        putValue(SHORT_DESCRIPTION,toolTipText);
        putValue(SMALL_ICON, icon);
        this.isPlugin=isPlugin;
        this.frame=frame;
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

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(isPlugin){            
            doAction(actionEvent);
        }
        else{
            int result = JOptionPane.showConfirmDialog(frame,
                    I18N.tr("You will do an action on a plugin used by the system.\n"
                            + "After that, OrbisGIS could be unstable.\n"
                            + "Do you confirm ?"),
                    I18N.tr("Security message"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                doAction(actionEvent);
            }
        }
       
    }    
    
    /**
     * Do the action
     * @param actionEvent 
     */
    private void doAction(ActionEvent actionEvent){
         // If this is done outside the SwingEventThread then a thread lock can occur
        try {
            action.actionPerformed(actionEvent);
        } catch (Exception ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

}
