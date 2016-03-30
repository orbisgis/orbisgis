/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsgroovyapi.output

import groovy.transform.AnnotationCollector
import groovy.transform.Field
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute
import org.orbisgis.wpsgroovyapi.attributes.OutputAttribute
import org.orbisgis.wpsgroovyapi.attributes.GeometryAttribute

/**
 * Geometry output annotation.
 * The Geometry is a complex data that represents a geometry.
 * As an output, this annotation should be placed just before the variable.
 *
 * The following fields must be defined (mandatory) :
 *  - title : String
 *       Title of the output. Normally available for display to a human.
 *
 * The following fields can be defined (optional) :
 *  - traducedTitles : LanguageString[]
 *      List of LanguageString containing the traduced titles.
 *  - resume : String
 *      Brief narrative description of the output. Normally available for display to a human.
 *  - traducedResumes : LanguageString[]
 *      List of LanguageString containing the traduced description.
 *  - keywords : String
 *      Array of keywords that characterize the output.
 *  - traducedKeywords : Keyword[]
 *      List of Keyword containing the keywords translations.
 *  - identifier : String
 *      Unambiguous identifier of the output. It should be a valid URI.
 *  - metadata : MetaData[]
 *      Reference to additional metadata about this item.
 *  - isDirectory : boolean
 *      Indicates that the RawData can be a directory.
 *  - isFile : boolean
 *      Indicates that the RawData can be a file.
 *  - geometryType : String[]
 *      Array of geometry type allowed. If no types are specified, accept all.
 *  - excludedTypes : String[]
 *      Array of the type not allowed for the geometry.
 *  - dimension : int
 *      Dimension of the geometry (can be 2 or 3).
 *
 * Usage example can be found at https://github.com/orbisgis/orbisgis/wiki/
 *
 * @author Sylvain PALOMINOS
 */
@AnnotationCollector([Field, GeometryAttribute, OutputAttribute, DescriptionTypeAttribute])
@interface GeometryOutput {}