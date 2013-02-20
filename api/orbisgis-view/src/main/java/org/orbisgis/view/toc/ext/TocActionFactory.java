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
package org.orbisgis.view.toc.ext;

import org.orbisgis.view.components.actions.ActionFactoryService;

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
    public static final String A_ADD_LAYER_GROUP = "A_ADD_LAYER_GROUP";

    // Style actions
    public static final String A_SIMPLE_EDITION = "A_SIMPLE_EDITION";
    public static final String A_ADVANCED_EDITION = "A_ADVANCED_EDITION";
    public static final String A_REMOVE_STYLE = "A_REMOVE_STYLE";
    public static final String A_EXPORT_STYLE = "A_EXPORT_STYLE";
}
