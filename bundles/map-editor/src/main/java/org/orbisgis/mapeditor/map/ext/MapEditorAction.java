/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.mapeditor.map.ext;

import org.orbisgis.viewapi.components.actions.ActionFactoryService;

/**
 * @author Nicolas Fortin
 */
public interface MapEditorAction extends ActionFactoryService<MapEditorExtension> {
    // Map editor Actions Toggle groups
    /**
     * Radio button group is
     * putValue(ActionTools.TOGGLE_GROUP,MapEditorAction.TOGGLE_GROUP_AUTOMATONS);
     */
    public static final String TOGGLE_GROUP_AUTOMATONS = "automatons";
    // Map editor Actions ID
    // Group
    public static final String A_MEASURE_GROUP = "A_MEASURE_GROUP";
    public static final String A_DRAWING_GROUP = "A_DRAWING_GROUP";

    // Built-in Automaton
    public static final String A_ZOOM_IN = "A_ZOOM_IN";
    public static final String A_ZOOM_OUT = "A_ZOOM_OUT";
    public static final String A_PAN = "A_PAN";
    public static final String A_SELECTION = "A_SELECTION";
    public static final String A_MEASURE_LINE = "A_MEASURE_LINE";
    public static final String A_MEASURE_POLYGON = "A_MEASURE_POLYGON";
    public static final String A_COMPASS = "A_COMPASS";
    public static final String A_FENCE = "A_FENCE";
    public static final String A_PICK_COORDINATES = "A_PICK_COORDINATES";
    public static final String A_INFO_TOOL = "A_INFO_TOOL";

    // Standard actions
    public static final String A_FULL_EXTENT = "A_FULL_EXTENT";
    public static final String A_ZOOM_SELECTION = "A_ZOOM_SELECTION";
    public static final String A_CLEAR_SELECTION = "A_CLEAR_SELECTION";
    public static final String A_DATA_SOURCE_FROM_SELECTION = "A_DATA_SOURCE_FROM_SELECTION";
    public static final String A_MAP_TREE = "A_MAP_TREE";
    public static final String A_MAP_EXPORT_IMAGE = "A_MAP_EXPORT_IMAGE";
}
