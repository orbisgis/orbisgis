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
package org.orbisgis.view.icons;

import java.awt.Image;
import javax.swing.ImageIcon;
import org.orbisgis.sif.icons.BaseIcon;
import org.orbisgis.sif.icons.SifIcon;


/**
 * @class OrbisGISIcon
 * Use this class to retrieve the data of an icon
 * This final class load icons only on request. This feature help to reduce
 * the loading time of OrbisGis. Moreover this class does'nt have to be updated
 * when new icons are added.
 * Icon files are placed in the resource package org.orbisgis.view.icons
 */
public final class OrbisGISIcon {
    private static BaseIcon iconManager = new BaseIcon();
    
    /**
     * This is a static class
     */
    private OrbisGISIcon() {
    }

    /**
     * Retrieve icon awt Image by its name
     * @param iconName The icon name, without extension. All icons are stored in the png format.
     * @return The Image content requested, or an Image corresponding to a Missing Resource
     */
    public static Image getIconImage(String iconName) { 
        return iconManager.getIconImage(OrbisGISIcon.class, iconName);
    }
    /**
     * Retrieve icon by its name
     * @param iconName The icon name, without extension. All icons are stored in the png format.
     * @return The ImageIcon requested, or an ImageIcon corresponding to a Missing Resource
     */
    public static ImageIcon getIcon(String iconName) {
        return iconManager.getIcon(OrbisGISIcon.class, iconName);        
    }
}
