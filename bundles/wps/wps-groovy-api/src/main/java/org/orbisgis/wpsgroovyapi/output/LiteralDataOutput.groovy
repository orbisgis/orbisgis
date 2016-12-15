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
import org.orbisgis.wpsgroovyapi.attributes.OutputAttribute
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute
import org.orbisgis.wpsgroovyapi.attributes.LiteralDataAttribute

/**
 * DataField output annotation.
 * The LiteralData represents a number or a string.
 * As an output, this annotation should be placed just before the variable.
 *
 * The following fields must be defined (mandatory) :
 *  - title : String[]
 *       Title of a process, input, and output. Normally available for display to a human. It is composed either a
 *       unique title or a translated title, its language, another title, its language ...
 *       i.e. title = "title" or tittle = ["titleFr", "fr", "titleEn", "en"]
 *
 * The following fields can be defined (optional) :
 *  - description : String[]
 *      Brief narrative description of a process, input, and output. Normally available for display to a human.It is
 *      composed either a unique description or a translated description, its language, another description, its language ...
 *      i.e. description = "description" or description = ["descriptionFr", "fr", "descriptionEn", "en"]
 *
 *  - keywords : String[]
 *      Array of keywords that characterize a process, its inputs, and outputs. Normally available for display to a
 *      human. It is composed of a succession of two String : the human readable keyword list coma
 *      separated and its language.
 *      i.e. keywords = ["the keyword 1,the keyword 2", "en",
 *                       "le mot clef 1, le mot clef 2", "fr"]
 *  - identifier : String
 *      Unambiguous identifier of a process, input, and output. It should be a valid URI.
 *
 *  - metadata : String[]
 *      Reference to additional metadata about this item. It is composed of a succession of three String : the metadata
 *      role, the metadata title and the href, coma separated.
 *      i.e. metadata = ["role1,title,href1",
 *                       "role2,title,href2"]
 *
 *  - defaultDomain : String[]
 *      This attribute contains the definition of the default possible value domain. The domains can be simple values
 *      ("foo", 2, 2.6589 ...) or value ranges with min, max and sometimes spacing (1;;42, 0;0.1;1 ...). If there is no
 *      default domain, all the values are accepted.
 *
 *      The possible value choices can have three different patterns :
 *          - value : A string representation of a possible value.
 *          - min;;max : A range of valid values with a min value and a max value.
 *          - min;spacing;max : A range of valid value with a spacing between two value, a min, and a max.
 *      i.e. :
 *      defaultDomain = ["0;;1, 1;1;100, 1000"]
 *      The allowed values are value1 (the default one), value2, value3 or a value between 0 to 1, a value between 1
 *      to 100 with a spacing of 1.
 *
 *  - validDomains : String[]
 *      This attribute contains the definition of all the possible value domain. The domains can be simple values
 *      ("foo", 2, 2.6589 ...) or value ranges with min, max and sometimes spacing (1;;42, 0;0.1;1 ...). If there is no
 *      default domain, the domains should be ignored.
 *
 *      This attribute is composed of a list of coma separated values. The values are the possible value choices which
 *      are define later. The very first value will be the default one. The possible value choices can have three
 *      different patterns :
 *          - value : A string representation of a possible value.
 *          - min;;max : A range of valid values with a min value and a max value.
 *          - min;spacing;max : A range of valid value with a spacing between two value, a min, and a max.
 *      i.e.
 *      validDomains = ["value1,value2,value3","0;;1,1;1;100"]
 *      The allowed values are value1 (the default one), value2, value3 or a value between 0 to 1, a value between 1
 *      to 100 with a spacing of 1.
 *
 * Usage example can be found at https://github.com/orbisgis/orbisgis/wiki/
 *
 * @author Sylvain PALOMINOS
 */
@AnnotationCollector([Field, LiteralDataAttribute, OutputAttribute, DescriptionTypeAttribute])
@interface LiteralDataOutput {}
