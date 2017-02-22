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
package org.orbisgis.geocatalogtree.api;

import org.orbisgis.sif.components.actions.ActionFactoryService;

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
    public static final String M_IMPORT_FOLDER = "M_IMPORT_FOLDER";

    // Save menu group
    public static final String M_SAVE = "M_SAVE";
    public static final String M_SAVE_FILE = "M_SAVE_FILE";
    public static final String M_SAVE_DB = "M_SAVE_DB";

    // Open attributes
    public static final String M_OPEN_ATTRIBUTES = "M_OPEN_ATTRIBUTES";

    // Remove item
    public static final String M_REMOVE = "M_REMOVE";
    public static final String M_REMOVE_INDEX = "M_REMOVE_INDEX";
    public static final String M_DROP_COLUMN = "M_DROP_COLUMN";

    // Create
    public static final String M_CREATE_SPATIAL_INDEX = "CREATE_SPATIAL_INDEX";
    
    public static final String M_CREATE_INDEX = "CREATE_INDEX";

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
