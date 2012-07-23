/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.sif.icons;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.orbisgis.sif.components.ColorPicker;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @package org.orbisgis.sif.icons
 * @brief Manage Icons loading
 */

/**
 * @class BaseIcon
 * @brief Use this class to retrieve the data of an icon
 * This final class load icons only on request. This feature help to reduce
 * the loading time of OrbisGis. Moreover this class does not have to be updated
 * when new icons are added.
 * Icon files are placed in the resource package org.orbisgis.sif.icons
 */


public class BaseIcon {
    private static final I18n i18n = I18nFactory.getI18n(BaseIcon.class);
    private Map<String,ImageIcon> loadedIcons=new HashMap<String,ImageIcon>();/*!< This map contain all loaded icons */
    
    private final ImageIcon ORBISGIS_MISSING_ICON = new ImageIcon(BaseIcon.class.getResource("remove.png")); /*!< Icon displayed when the requested icon is not found */
    
    private final Logger LOG = Logger.getLogger(BaseIcon.class); /*!< Logger of SifIcon */
   
    
    /**
     * Retrieve icon awt Image by its name
     * @param iconName The icon name, without extension. All icons are stored in the png format.
     * @return The Image content requested, or an Image corresponding to a Missing Resource
     */
    public Image getIconImage(Class<?> loader,String iconName) { 
        return getIcon(loader,iconName).getImage();
    }
    
    
    /**
     * Retrieve icon by its name
     * @param iconName The icon name, without extension. All icons are stored in the png format.
     * @return The ImageIcon requested, or an ImageIcon corresponding to a Missing Resource
     */
    public ImageIcon getIcon(Class<?> loader,String iconName) {
        if(!loadedIcons.containsKey(iconName)) {
            //This is the first request for this icon
            String resourceName = iconName+".png";
            URL url = loader.getResource(resourceName);
            if(url!=null) {
                ImageIcon newIcon = new ImageIcon(url);
                loadedIcons.put(iconName, newIcon);
                return newIcon;
            } else {
                LOG.warn(i18n.tr("The following icon is not found : {0}",resourceName));
                //The next time, return directly the missing icon
                loadedIcons.put(iconName, ORBISGIS_MISSING_ICON); 
                return ORBISGIS_MISSING_ICON;
            }            
        } else {
            //Icon was already loaded, return its content
            return loadedIcons.get(iconName);
        }
    }
}
