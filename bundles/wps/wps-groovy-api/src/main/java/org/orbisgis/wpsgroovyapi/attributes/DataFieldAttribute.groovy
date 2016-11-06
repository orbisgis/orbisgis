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

package org.orbisgis.wpsgroovyapi.attributes

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Attributes for the DataField complex data.
 * The DataField is a complex data that represents a DataSource field (i.e. a column of a table).
 * It is linked to a DataStore and its allowed types can be specified.
 *
 * The following fields must be defined (mandatory) :
 *  - variableReference : String
 *      Name of the variable of the DataStore.
 *
 * The following fields can be defined (optional) :
 *  - fieldTypes : String[]
 *      Array of the types allowed. If no types are specified, accepts all.
 *  - excludedTypes : String[]
 *      Array of the type forbidden. If no types are specified, accept all.
 *  - multiSelection : boolean
 *      Enable or not the user to select more than one field. Disabled by default.
 *
 * @author Sylvain PALOMINOS
 */
@Retention(RetentionPolicy.RUNTIME)
@interface DataFieldAttribute {

    /** Name of the variable of the DataStore.*/
    String variableReference()

    /** Array of the type allowed for the data field. If no types are specified, accept all.*/
    String[] fieldTypes() default []

    /** Array of the type not allowed for the data field.*/
    String[] excludedTypes() default []

    /** Enable or not the user to select more than one field.*/
    boolean multiSelection() default false



    /********************/
    /** default values **/
    /********************/
    public static final String[] defaultFieldType = []
    public static final String[] defaultExcludedType = []
    public static final boolean defaultMultiSelection = []
}