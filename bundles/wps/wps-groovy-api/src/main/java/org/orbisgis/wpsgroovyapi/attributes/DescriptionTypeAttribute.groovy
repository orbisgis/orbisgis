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

package org.orbisgis.wpsgroovyapi.attributes


import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Attributes of the descriptive elements of processes, inputs and outputs.
 * Other descriptive information shall be recorded in the Metadata element in the form of simple links with an
 * appropriate role identifier.
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
 * @author Sylvain PALOMINOS
 */
@Retention(RetentionPolicy.RUNTIME)
@interface DescriptionTypeAttribute {

    /**
     * Title of a process, input, and output. Normally available for display to a human. It is composed either a unique
     * title or a translated title, its language, another title, its language ...
     * i.e. title = "title" or title = ["titleFr", "fr", "titleEn", "en"]
     */
    String[] title() default[]

    /**
     * Brief narrative description of a process, input, and output. Normally available for display to a human. It is
     * composed either a unique description or a translated description, its language, another description, its language ...
     * i.e. description = "description" or description = ["descriptionFr", "fr", "descriptionEn", "en"]
     */
    String[] description() default []

    /** Array of keywords that characterize a process, its inputs, and outputs. Normally available for display to a
     * human. It is composed of a succession of two String : the human readable keyword list coma
     * separated and its language.
     * i.e. keywords = ["the keyword 1,the keyword 2", "en",
     *                  "le mot clef 1, le mot clef 2", "fr"]
     */
    String[] keywords() default []

    /** Unambiguous identifier of a process, input, and output. */
    String identifier() default ""

    /** Reference to additional metadata about this item. It is composed of a succession of three String : the metadata
     * role, the metadata title and the href, coma separated.
     * i.e. metadata = ["role1,title,href1",
     *                  "role2,title,href2"]
     */
    String[] metadata() default []
}