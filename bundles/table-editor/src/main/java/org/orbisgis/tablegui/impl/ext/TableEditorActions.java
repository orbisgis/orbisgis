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
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.tablegui.impl.ext;


import org.orbisgis.sif.components.actions.ActionFactoryService;

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
    //GROUP READ
    public static final String A_REFRESH = "A_REFRESH";
    public static final String A_PREVIOUS_SELECTION = "A_PREVIOUS_SELECTION";
    public static final String A_NEXT_SELECTION = "A_NEXT_SELECTION";

    public static final String LGROUP_READ = "LGROUP_READ";
    public static final String LGROUP_EDITION = "LGROUP_EDITION";
    public static final String LGROUP_MODIFICATION_GROUP = "LGROUP_MODIFICATION_GROUP";
}
