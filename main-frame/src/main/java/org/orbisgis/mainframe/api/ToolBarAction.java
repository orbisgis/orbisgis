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

package org.orbisgis.mainframe.api;

import org.orbisgis.sif.components.actions.ActionFactoryService;

/**
 * Implement this interface to define additional ToolBar items.
 * ToolBar items can be put in the same group by using the property
 * @see org.orbisgis.sif.components.actions.ActionTools#LOGICAL_GROUP
 * Only built-ins toolbar items are listed here in order to use it on the {@link org.orbisgis.sif.components.actions.ActionTools#INSERT_AFTER_MENUID}
 * @author Nicolas Fortin
 */
public interface ToolBarAction  extends ActionFactoryService<MainWindow> {
    // Drawing toolbar items
    public static final String DRAW_LINE = "DRAW_LINE";
    public static final String DRAW_MULTI_LINE = "DRAW_MULTI_LINE";
    public static final String DRAW_POINT = "DRAW_POINT";
    public static final String DRAW_MULTI_POINT = "DRAW_MULTI_POINT";
    public static final String DRAW_POLYGON = "DRAW_POLYGON";
    public static final String DRAW_MULTI_POLYGON = "DRAW_MULTI_POLYGON";
    public static final String DRAW_AUTO_POLYGON = "DRAW_AUTO_POLYGON";
    public static final String DRAW_CHANGE_COORDINATE = "DRAW_CHANGE_COORDINATE";
    public static final String DRAW_COPY_LINE = "DRAW_COPY_LINE";
    public static final String DRAW_COPY_POINT = "DRAW_COPY_POINT";
    public static final String DRAW_COPY_POLYGON = "DRAW_COPY_POLYGON";
    public static final String DRAW_CUT_POLYGON = "DRAW_CUT_POLYGON";
    public static final String DRAW_MOVE_GEOMETRY = "DRAW_MOVE_GEOMETRY";
    public static final String DRAW_MOVE_VERTEX = "DRAW_MOVE_VERTEX";
    public static final String DRAW_SPLIT_LINESTRING = "DRAW_SPLIT_LINESTRING";
    public static final String DRAW_SPLIT_LINE_BY_LINE = "DRAW_SPLIT_LINE_BY_LINE";
    public static final String DRAW_SPLIT_POLYGON = "DRAW_SPLIT_POLYGON";
    public static final String DRAW_VERTEX_ADDITION = "DRAW_VERTEX_ADDITION";
    public static final String DRAW_VERTEX_DELETION = "DRAW_VERTEX_DELETION";
    public static final String DRAW_CANCEL = "DRAW_CANCEL";
    public static final String DRAW_UNDO = "DRAW_UNDO";
    public static final String DRAW_REDO = "DRAW_REDO";
    public static final String DRAW_DELETE = "DRAW_DELETE";
    public static final String DRAW_SAVE = "DRAW_SAVE";
    public static final String DRAW_STOP = "DRAW_STOP";
    // Toolbar items group
    public static final String DRAWING_GROUP = "DRAWING_GROUP";
}
