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
package org.orbisgis.view.components.menubar;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Persistant menu management in OrbisGIS. The final menu key is unique.
 * TODO This component register as a service host on future plugin system
 */

public class MenuBarManager {
    //This is a dev error, useless to translate
    private final static String PARENT_NOT_EXISTS = "Parent menu must be inserted before the sub-menus";
    private JMenuBar rootBar = new JMenuBar();
    //Store a flat version of menu containers
    private Map<String,MenuProperties> menuContainers = new HashMap<String,MenuProperties>();
    //TODO Store Orphan MenuItems to add them when the menu container will be avaible
    /**
     * Translate the characted following & in the label by a mnemonic
     * @param menuItem The menu item
     */
    private void setMnemonic(JMenuItem menuItem) {
        String menuLabel = menuItem.getText();
        if(menuLabel.contains("&")) {
            int charpos=menuLabel.indexOf("&");
            if(menuLabel.length()>charpos+1) {
                if(!menuLabel.substring(charpos+1,charpos+2).equals("&")) {
                    menuItem.setMnemonic(menuLabel.substring(charpos+1,charpos+2).toCharArray()[0]);
                }
                menuItem.setText(menuLabel.replaceFirst("&",""));
            }
        }
    }
    /**
     * Add a menu container
     * @param parentKey The parent menu key, empty if directly on menu bar
     * @param menu The Menu properties bean
     * @throws IllegalArgumentException if the parentKey is not found 
     * or if the menu key already exists
     */
    public void addMenu(String parentKey,MenuProperties menu) {
        if(menuContainers.containsKey(menu.getKey())) {
            throw new IllegalArgumentException("The menu key already exists.");
        }
        setMnemonic(menu.getMenu());
        if(parentKey.isEmpty()) {
            rootBar.add(menu.getMenu());
        } else {
            MenuProperties parentMenu =menuContainers.get(parentKey);
            if(parentMenu==null) {
                throw new IllegalArgumentException(PARENT_NOT_EXISTS);
            }
            parentMenu.getMenu().add(menu.getMenu());
        }
        menuContainers.put(menu.getKey(),menu);
    }
    /**
     * Add a menu item
     * @param parentKey The parent menu key, empty if directly on menu bar
     * @param menuItem The Menu properties bean
     * @throws IllegalArgumentException if the parentKey is not found
     */
    public void addMenuItem(String parentKey,MenuItemProperties menuItem) {
        MenuProperties parentMenu =menuContainers.get(parentKey);
        if(parentMenu==null) {
            throw new IllegalArgumentException(PARENT_NOT_EXISTS);
        }
        setMnemonic(menuItem.getMenuItem());
        parentMenu.getMenu().add(menuItem.getMenuItem());
    }

    /**
     * 
     * @return The swing component that contain the menu bar
     */
    public JMenuBar getRootBar() {
        return rootBar;
    }
    
    
}
