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

package org.orbisgis.wpsgroovyapi.input

import groovy.transform.AnnotationCollector
import groovy.transform.Field
import org.orbisgis.wpsgroovyapi.attributes.DataFieldAttribute
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute

/**
 * DataField input annotation.
 * The DataField is a complex data that represents a DataSource field (i.e. a column of a table).
 * As an input, this annotation should be placed just before the variable.
 *
 * The following fields must be defined (mandatory) :
 *  - title : String
 *       Title of the input. Normally available for display to a human.
 *  - variableReference : String
 *      Name of the variable of the DataStore.
 *
 * The following fields can be defined (optional) :
 *  - traducedTitles : LanguageString[]
 *      List of LanguageString containing the traduced titles.
 *  - resume : String
 *      Brief narrative description of the input. Normally available for display to a human.
 *  - traducedResumes : LanguageString[]
 *      List of LanguageString containing the traduced description.
 *  - keywords : String
 *      Array of keywords that characterize the input.
 *  - traducedKeywords : Keyword[]
 *      List of Keyword containing the keywords translations.
 *  - identifier : String
 *      Unambiguous identifier of the input. It should be a valid URI.
 *  - metadata : MetaData[]
 *      Reference to additional metadata about this item.
 *  - minOccurs : int
 *      Minimum number of times that values for this parameter are required. 0 means the input is optional.
 *  - maxOccurs : int
 *      Maximum number of times that this parameter may be present.
 *  - fieldTypes : String[]
 *      Array of the types allowed. If no types are specified, accepts all.
 *  - excludedTypes : String[]
 *      Array of the type forbidden. If no types are specified, accept all.
 *  - multiSelection : boolean
 *      Enable or not the user to select more than one field. Disabled by default.
 *
 * Usage example can be found at https://github.com/orbisgis/orbisgis/wiki/
 *
 * @author Sylvain PALOMINOS
 */
@AnnotationCollector([Field, DataFieldAttribute, InputAttribute, DescriptionTypeAttribute])
@interface DataFieldInput {}