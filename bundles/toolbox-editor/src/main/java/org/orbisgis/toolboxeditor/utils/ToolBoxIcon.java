/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.toolboxeditor.utils;

import org.orbisgis.sif.icons.BaseIcon;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Use this class to retrieve the data of an icon
 * @author Sylvain PALOMINOS
 * @author Erwan Bocher
 **/

public class ToolBoxIcon {

    public static final String JDBC_TABLE = "jdbc_table";
    public static final String FLAT_FILE = "flatfile";
    public static final String GEO_FILE = "geofile";
    public static final String BROWSE = "browse";
    public static final String OPTIONS = "options";
    public static final String JDBC_COLUMN = "jdbc_column";
    public static final String ENUMERATION = "enumeration";
    public static final String JDBC_VALUE = "jdbc_value";
    public static final String STRING = "string";
    public static final String NUMBER = "number";
    public static final String BOOLEAN = "boolean";
    public static final String UNDEFINED = "undefined";
    public static final String PASTE = "paste";
    public static final String RAW_DATA = "rawdata";
    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String TOGGLE_MODE = "process_batch";
    public static final String LOCALHOST = "localhost";
    public static final String DISTANT_HOST = "database_distant";
    public static final String DISTANT_HOST_INVALID = "database_distant_invalid";
    public static final String FOLDER_CLOSED = "folder_closed";
    public static final String PROCESS = "process";
    public static final String PROCESS_INVALID = "process_invalid";
    public static final String ERROR = "error";
    public static final String FOLDER_OPEN= "folder_open";
    public static final String ORBIS_TOOLBOX ="orbistoolbox";
    public static final String REFRESH ="refresh";
    public static final String FOLDER_ADD ="folder_add";
    public static final String SCRIPT_ADD ="script_add";
    public static final String EXECUTE ="execute";
    public static final String REMOVE ="remove";
    public static final String LOG = "log";
    public static final String STOP = "stop";
    public static final String PROCESS_RUNING ="process_running";
    public static final String PROCESS_ERROR ="process_error";
    public static final String BTNRIGHT ="btnright";
    public static final String BTNDOWN ="btndown";

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
     * Retrieve icon by its name or its full file path.
     *
     * @param iconName The icon name, without extension or icon file path. All icons are stored in the png format.
     * @return The ImageIcon requested, or an ImageIcon corresponding to a Missing Resource
     */
    public static ImageIcon getIcon(String iconName) {
        //If the iconName is a file path, load it
        File f = new File(iconName);
        if(f.exists() && f.isFile()){
            return new ImageIcon(iconName);
        }
        //It the iconName is the icon name, load it
        else {
            if (ToolBoxIcon.class.getResource(iconName+".png") != null) {
                return iconManager.getIcon(ToolBoxIcon.class, iconName);
            }
            else{
                return null;
            }
        }
    }
}
