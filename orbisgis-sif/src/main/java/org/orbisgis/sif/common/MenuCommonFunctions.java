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
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * Common tools to Swing Menu
 */
public class MenuCommonFunctions {
    public static final Character MNEMONIC_CHARACTER = '&';
    public static final String MNEMONIC_STRING = MNEMONIC_CHARACTER.toString();
    /**
     * Static class
     */
    private MenuCommonFunctions() {
        
    }

    /**
     * Translate the character following MNEMONIC_CHARACTER in the label by a mnemonic
     * @param action The action instance
     */
    public static void setMnemonic(Action action) {
        String actionLabel = (String)action.getValue(Action.NAME);
        if(actionLabel!=null) {
            int charPosition=getMnemonicCharPos(actionLabel);
            if(charPosition>=0) {
                action.putValue(Action.MNEMONIC_KEY,new Integer(Character.toUpperCase(actionLabel.charAt(charPosition+1))));
                // Update Name
                action.putValue(Action.NAME,clearMnemonic(charPosition,actionLabel));
            }
        }
    }
    /**
     * Translate the character following MNEMONIC_CHARACTER in the label by a mnemonic
     * @param actionComponent The menu item
     */
    public static void setMnemonic(AbstractButton actionComponent) {
        String componentLabel = actionComponent.getText();
        int charPosition=getMnemonicCharPos(componentLabel);
        if(charPosition>=0) {
            actionComponent.setMnemonic(Character.toUpperCase(componentLabel.charAt(charPosition+1)));
            actionComponent.setText(clearMnemonic(charPosition,componentLabel));
        }
    }

    /**
     * remove mnemonic hint. ex: "A &Label" become "A Label"
     * @param mnemonicPosition
     * @param originalText
     * @return
     */
    private static String clearMnemonic(int mnemonicPosition,String originalText) {
        String removedSpecChar = originalText.substring(0,mnemonicPosition)+originalText.substring(mnemonicPosition+1,originalText.length());
        removedSpecChar = removedSpecChar.replace(MNEMONIC_STRING+MNEMONIC_STRING,MNEMONIC_STRING);
        return removedSpecChar;
    }
    /**
     * @param label
     * @return Position of the mnemonic character, -1 if not found
     */
    private static int getMnemonicCharPos(String label) {
        int charPosition=label.indexOf(MNEMONIC_CHARACTER);
        while(charPosition!=-1 && label.length()>charPosition+1) {
            if(label.charAt(charPosition+1)!=new Character(MNEMONIC_CHARACTER)) {
                    return charPosition;
            } else {
                    //found &&
                    charPosition = label.indexOf(MNEMONIC_CHARACTER,charPosition+2);
            }
        }
        return -1;
    }
    /**
     * @param menu
     * @param menuItem
     * @return True if another item was found with the same actionCommand 
     */
    private static boolean recursiveUpdateOrInsertMenuItem(MenuElement menu, JMenuItem menuItem,boolean hideOnUpdate) {
            boolean updated = false;
            for(MenuElement menuEl : menu.getSubElements()) {
                    if(menuEl instanceof JMenuItem) {
                            JMenuItem subMenuItem = (JMenuItem)menuEl;
                            String actionCommand = subMenuItem.getActionCommand();
                            if(actionCommand.equals(menuItem.getActionCommand())) {
                                    if (hideOnUpdate) {
                                            subMenuItem.setVisible(false);
                                    } else {
                                            for (ActionListener listener : menuItem.getActionListeners()) {
                                                    subMenuItem.addActionListener(listener);
                                            }
                                    }
                                    updated = true;
                            }
                    } else {
                            updated = updated || recursiveUpdateOrInsertMenuItem(menuEl,menuItem,hideOnUpdate);
                    }
            }           
            return updated;
    }

        /**
         * Depending on menuItem actionCommand, if a sub menu of menu has the
         * same actionCommand then the already inserted menu item receive all
         * the action listeners of the provided menuItem
         *
         * @param menu Root of menu items
         * @param menuItem New menu item to insert
         */
        public static void updateOrInsertMenuItem(JPopupMenu menu, JMenuItem menuItem) {
                updateOrInsertMenuItem(menu, menuItem, false);
        }

        /**
         * Depending on menuItem actionCommand, if a sub menu of menu has the
         * same actionCommand then the already inserted menu item receive all
         * the action listeners of the provided menuItem
         *
         * @param menu Root of menu items
         * @param menuItem New menu item to insert
         * @param hideOnUpdate If this action already exists, hide it from the
         * menu
         */
        public static void updateOrInsertMenuItem(JPopupMenu menu, JMenuItem menuItem, boolean hideOnUpdate) {
                if (!recursiveUpdateOrInsertMenuItem(menu, menuItem, hideOnUpdate)) {
                        menu.add(menuItem);
                }
        }
}
