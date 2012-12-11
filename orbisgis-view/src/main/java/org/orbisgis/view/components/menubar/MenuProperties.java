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
package org.orbisgis.view.components.menubar;

import javax.swing.JMenu;
import org.orbisgis.sif.common.ContainerItemProperties;

/**
 * The Menu container bean is used in persistant menu (not popup).
 * This beans separate the final item key and the visible label of the item
 * It will include optional additionnal functionalities like themes
 * @note Do not use the Swing Mnemonic, the key shortcut would be set in
 * the translated label. Use the prefix symbol & to set the shortcut,
 * escape with two &&
 */


public class MenuProperties extends ContainerItemProperties {
    private JMenu menu;
    public MenuProperties(String key, JMenu menu) {
        super(key, menu.getText());
        this.menu = menu;
    }
    /**
     * 
     * @return  The Menu instance
     */
    public JMenu getMenu() {
        return menu;
    }
    
}
