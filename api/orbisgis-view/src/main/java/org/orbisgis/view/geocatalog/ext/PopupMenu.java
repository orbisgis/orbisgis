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
package org.orbisgis.view.geocatalog.ext;

import org.orbisgis.view.components.actions.ActionFactoryService;

/**
 * PopupMenu service of GeoCatalog
 * @author Nicolas Fortin
 */
public interface PopupMenu extends ActionFactoryService<PopupTarget> {
    // The Add menu group
    public static final String M_ADD = "M_ADD";
    public static final String M_ADD_FILE = "M_ADD_FILE";
    public static final String M_ADD_FOLDER = "M_ADD_FOLDER";
    public static final String M_ADD_DB = "M_ADD_DB";
    public static final String M_ADD_WMS = "M_ADD_WMS";
    // The import menu group
    public static final String M_IMPORT = "M_IMPORT";
    public static final String M_IMPORT_FILE = "M_IMPORT_FILE";

    // Save menu group
    public static final String M_SAVE = "M_SAVE";
    public static final String M_SAVE_FILE = "M_SAVE_FILE";
    public static final String M_SAVE_DB = "M_SAVE_DB";
    // Open attributes
    public static final String M_OPEN_ATTRIBUTES = "M_OPEN_ATTRIBUTES";
    // Remove source
    public static final String M_REMOVE = "M_REMOVE";

    public static final String M_CLEAR_CATALOG = "M_CLEAR_CATALOG";
    // Refresh geocatalog
    public static final String M_REFRESH = "M_REFRESH";
    /**
     * Grouping keys : Group of menus used to add sources
     */
    public static final String GROUP_ADD = "ADD";
    /**
     * Grouping keys : Group of menus used to import sources
     */
    public static final String GROUP_IMPORT = "IMPORT";
    /**
     * Grouping keys : Group of menus used to display table of sources
     */
    public static final String GROUP_OPEN = "OPEN";
    /**
     * Grouping keys : Group of menus used to close things
     */
    public static final String GROUP_CLOSE = "CLOSE";
}
