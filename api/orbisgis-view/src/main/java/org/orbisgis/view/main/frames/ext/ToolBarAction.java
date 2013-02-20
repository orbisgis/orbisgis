/*
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

package org.orbisgis.view.main.frames.ext;

import org.orbisgis.view.components.actions.ActionFactoryService;

/**
 * Implement this interface to define additional ToolBar items.
 * ToolBar items can be put in the same group by using the property
 * @see org.orbisgis.view.components.actions.ActionTools#LOGICAL_GROUP
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
    public static final String DRAW_SPLIT_POLYGON = "DRAW_SPLIT_POLYGON";
    public static final String DRAW_VERTEX_ADDITION = "DRAW_VERTEX_ADDITION";
    public static final String DRAW_VERTEX_DELETION = "DRAW_VERTEX_DELETION";


}
