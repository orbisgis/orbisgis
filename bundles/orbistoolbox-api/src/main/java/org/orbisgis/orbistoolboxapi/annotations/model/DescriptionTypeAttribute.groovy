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

package org.orbisgis.orbistoolboxapi.annotations.model

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation for the DescriptionType declaration.
 *
 * @author Sylvain PALOMINOS
 */
@Retention(RetentionPolicy.RUNTIME)
@interface DescriptionTypeAttribute {
    /** Default value for the abstrac attribute */
    String defaultAbstrac = ""
    /** Default value for the keywords attribute */
    String defaultKeywords = ""
    /** Default value for the identifier attribute */
    String defaultIdentifier = ""
    /** Default value for the metadata attribute */
    MetadataAttribute[] defaultMetadata = []


    /** Title of a process, input, and output. Normally available for display to a human. */
    String title()
    /** Brief narrative description of a process, input, and output. Normally available for display to a human. */
    String abstrac() default ""
    /** Coma separated keywords that characterize a process, its inputs, and outputs. */
    String keywords() default ""
    /** Unambiguous identifier of a process, input, and output. */
    String identifier() default ""
    /** Reference to additional metadata about this item. */
    MetadataAttribute[] metadata() default []
}