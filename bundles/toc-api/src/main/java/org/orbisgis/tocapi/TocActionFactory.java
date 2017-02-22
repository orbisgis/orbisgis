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
package org.orbisgis.tocapi;


import org.orbisgis.sif.components.actions.ActionFactoryService;

/**
 * Toc additional actions
 * @author Nicolas Fortin
 */
public interface TocActionFactory extends ActionFactoryService<TocExt> {
    // Layer actions
    public static final String A_REMOVE_LAYER = "A_REMOVE_LAYER";
    public static final String A_ZOOM_TO = "A_ZOOM_TO";
    public static final String A_ZOOM_TO_SELECTION = "A_ZOOM_TO_SELECTION";
    public static final String A_CLEAR_SELECTION = "A_CLEAR_SELECTION";
    public static final String A_IMPORT_STYLE = "A_IMPORT_STYLE";
    public static final String A_ADD_STYLE = "A_ADD_STYLE";
    public static final String A_OPEN_ATTRIBUTES = "A_OPEN_ATTRIBUTES";
    public static final String A_EDIT_GEOMETRY = "A_EDIT_GEOMETRY";
    public static final String A_STOP_EDIT_GEOMETRY = "A_STOP_EDIT_GEOMETRY";
    public static final String A_SAVE_EDIT_GEOMETRY = "A_SAVE_EDIT_GEOMETRY";
    public static final String A_CANCEL_EDIT_GEOMETRY = "A_CANCEL_EDIT_GEOMETRY";
    public static final String A_ADD_LAYER_GROUP = "A_ADD_LAYER_GROUP";
    public static final String A_WMS_LAYER = "A_WMS_LAYER";

    // Style actions
    public static final String A_ADD_LEGEND = "A_ADD_LEGEND";
    public static final String A_SIMPLE_EDITION = "A_SIMPLE_EDITION";
    public static final String A_ADVANCED_EDITION = "A_ADVANCED_EDITION";
    public static final String A_REMOVE_STYLE = "A_REMOVE_STYLE";
    public static final String A_EXPORT_STYLE = "A_EXPORT_STYLE";

    // Logic groups
    public static final String G_ATTRIBUTES = "G_ATTRIBUTES";
    public static final String G_DRAWING = "G_DRAWING";
    public static final String G_LAYER_GROUP = "G_LAYER_GROUP";
    public static final String G_REMOVE = "G_REMOVE";
    public static final String G_SELECTION = "G_SELECTION";
    public static final String G_STYLE = "G_STYLE";
    public static final String G_ZOOM = "G_ZOOM";

    // Menu group
    public static final String G_ADD = "G_ADD";

}
