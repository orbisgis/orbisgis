/*
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

package org.orbisgis.view.table.ext;

import org.orbisgis.viewapi.components.actions.ActionFactoryService;

/**
 * @author Nicolas Fortin
 */
public interface TableEditorActions extends ActionFactoryService<SourceTable> {
    //LGROUP_EDITION
    public static final String A_EDITION = "A_EDITION";
    public static final String A_SAVE = "A_SAVE";
    public static final String A_UNDO = "A_UNDO";
    public static final String A_REDO = "A_REDO";
    public static final String A_CANCEL = "A_CANCEL";
    //LGROUP_MODIFICATION_GROUP
    public static final String A_ADD_FIELD = "A_ADD_FIELD";
    public static final String A_ADD_ROW = "A_ADD_ROW";
    public static final String A_REMOVE_ROW = "A_REMOVE_ROW";

    public static final String LGROUP_EDITION = "LGROUP_EDITION";
    public static final String LGROUP_MODIFICATION_GROUP = "LGROUP_MODIFICATION_GROUP";
}
