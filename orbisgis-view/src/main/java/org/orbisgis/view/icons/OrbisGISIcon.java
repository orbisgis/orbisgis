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
package org.orbisgis.view.icons;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.orbisgis.utils.I18N;

/**
 * @package org.orbisgis.view.icons
 * @brief Manage Icons loading
 */

/**
 * @class OrbisGISIcon
 * @brief Use this class to retrieve the data of an icon
 * This final class load icons only on request. This feature help to reduce
 * the loading time of OrbisGis. Moreover this class does'nt have to be updated
 * when new icons are added.
 * Icon files are placed in the resource package org.orbisgis.view.icons
 */


public final class OrbisGISIcon {
    private static Map<String,ImageIcon> loadedIcons=new HashMap<String,ImageIcon>();/*!< This map contain all loaded icons */
    
    private static final ImageIcon ORBISGIS_MISSING_ICON = new ImageIcon(OrbisGISIcon.class.getResource("remove.png")); /*!< Icon displayed when the requested icon is not found */
    
    private static final Logger LOG = Logger.getLogger(OrbisGISIcon.class); /*!< Logger of OrbisGISIcon */
    
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
        return getIcon(iconName).getImage();
    }
    /**
     * Retrieve icon by its name
     * @param iconName The icon name, without extension. All icons are stored in the png format.
     * @return The ImageIcon requested, or an ImageIcon corresponding to a Missing Resource
     */
    public static ImageIcon getIcon(String iconName) {
        if(!loadedIcons.containsKey(iconName)) {
            //This is the first request for this icon
            URL url = OrbisGISIcon.class.getResource(iconName+".png");
            if(url!=null) {
                ImageIcon newIcon = new ImageIcon(url);
                loadedIcons.put(iconName, newIcon);
                return newIcon;
            } else {
                LOG.warn(I18N.getString("org.orbisgis.view.icons.OrbisGISIcon.icon_not_found")+" : "+iconName);
                return ORBISGIS_MISSING_ICON;
            }            
        } else {
            //Icon was already loaded, return its content
            return loadedIcons.get(iconName);
        }
    }
}
