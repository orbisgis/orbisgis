/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE
 * team located in University of South Brittany, Vannes.
 *
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
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

package org.orbisgis.wpsgroovyapi.input

import groovy.transform.AnnotationCollector
import groovy.transform.Field
import org.orbisgis.wpsgroovyapi.attributes.EnumerationAttribute
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute

/**
 * Enumeration input annotation.
 * The Enumeration complex data represents a selection of values from a predefined list.
 * As an input, this annotation should be placed just before the variable.
 *
 * The following fields must be defined (mandatory) :
 *  - title : String
 *       Title of the input. Normally available for display to a human.
 *  - values : String[]
 *      List of possible values.
 *
 * The following fields can be defined (optional) :
 *  - resume : String
 *      Brief narrative description of the input. Normally available for display to a human.
 *  - keywords : String
 *      Coma separated keywords that characterize the input.
 *  - identifier : String
 *      Unambiguous identifier of the input. It should be a valid URI.
 *  - metadata : MetaData[]
 *      Reference to additional metadata about this item.
 *  - minOccurs : int
 *      Minimum number of times that values for this parameter are required. 0 means the input is optional.
 *  - maxOccurs : int
 *      Maximum number of times that this parameter may be present.
 *  - multiSelection : boolean
 *      Allow or not to select more than one value.
 *  - isEditable : boolean
 *      Enable or not the user to use its own value.
 *  - names : String[]
 *      Displayable name of the values. If not specified, use the values as name.
 *  - defaultValues : String[]
 *      Default selected values, can be empty.
 *
 * Usage example can be found at https://github.com/orbisgis/orbisgis/wiki/
 *
 * @author Sylvain PALOMINOS
 */
@AnnotationCollector([Field, EnumerationAttribute, InputAttribute, DescriptionTypeAttribute])
@interface EnumerationInput {}