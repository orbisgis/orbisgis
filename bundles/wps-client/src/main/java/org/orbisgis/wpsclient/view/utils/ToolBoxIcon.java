/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsclient.view.utils;

import org.orbisgis.sif.icons.BaseIcon;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Use this class to retrieve the data of an icon
 * @author Sylvain PALOMINOS
 **/

public class ToolBoxIcon {

    public static final String DATA_STORE = "datastore";
    public static final String FLAT_FILE = "flatfile";
    public static final String GEO_FILE = "geofile";
    public static final String GEOCATALOG = "geocatalog";
    public static final String BROWSE = "browse";
    public static final String OPTIONS = "options";
    public static final String DATA_FIELD = "datafield";
    public static final String ENUMERATION = "enumeration";
    public static final String FIELD_VALUE = "fieldvalue";

    private static BaseIcon iconManager = new BaseIcon(LoggerFactory.getLogger(ToolBoxIcon.class));

    /**
     * Retrieve icon awt Image by its name
     * @param iconName The icon name, without extension. All icons are stored in the png format.
     * @return The Image content requested, or an Image corresponding to a Missing Resource
     */
    public static Image getIconImage(String iconName) {
        return iconManager.getIconImage(ToolBoxIcon.class, iconName);
    }
    /**
     * Retrieve icon by its name
     * @param iconName The icon name, without extension. All icons are stored in the png format.
     * @return The ImageIcon requested, or an ImageIcon corresponding to a Missing Resource
     */
    public static ImageIcon getIcon(String iconName) {
        return iconManager.getIcon(ToolBoxIcon.class, iconName);
    }
}
