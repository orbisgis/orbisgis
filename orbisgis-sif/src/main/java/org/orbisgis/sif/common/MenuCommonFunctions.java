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
package org.orbisgis.sif.common;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * Common tools to Swing Menu
 */
public class MenuCommonFunctions {
    /**
     * Static class
     */
    private MenuCommonFunctions() {
        
    }
    
    /**
     * Translate the character following & in the label by a mnemonic
     * @param actionComponent The menu item
     */
    public static void setMnemonic(AbstractButton actionComponent) {
        String componentLabel = actionComponent.getText().toLowerCase();
        int charpos=componentLabel.indexOf("&");
        if(charpos!=-1 && componentLabel.length()>charpos+1) {
                if(componentLabel.charAt(charpos+1)!=KeyEvent.VK_AMPERSAND) {
                        actionComponent.setMnemonic(componentLabel.charAt(charpos+1));
                }
                actionComponent.setText(componentLabel.replaceFirst("&",""));
        }
    }
    
    /**
     * @param menu
     * @param menuItem
     * @return True if another item was found with the same actionCommand 
     */
    private static boolean recursiveUpdateOrInsertMenuItem(MenuElement menu, JMenuItem menuItem) {
            boolean updated = false;
            for(MenuElement menuEl : menu.getSubElements()) {
                    if(menuEl instanceof JMenuItem) {
                            JMenuItem subMenuItem = (JMenuItem)menuEl;
                            String actionCommand = subMenuItem.getActionCommand();
                            if(actionCommand.equals(menuItem.getActionCommand())) {
                                    for(ActionListener listener : menuItem.getActionListeners()) {
                                            subMenuItem.addActionListener(listener);
                                    }
                                    updated = true;
                            }
                    } else {
                            updated = updated || recursiveUpdateOrInsertMenuItem(menuEl,menuItem);
                    }
            }           
            return updated;
    }
    /**
     * Depending on menuItem actionCommand, if a sub menu of menu
     * has the same actionCommand then the already inserted menu item receive
     * all the action listeners of the provided menuItem
     * @param menu Root of menu items
     * @param menuItem New menu item to insert
     */
    public static void updateOrInsertMenuItem(JPopupMenu menu, JMenuItem menuItem) {
            if(!recursiveUpdateOrInsertMenuItem(menu,menuItem)) {
                    menu.add(menuItem);
            }    
    }
}
