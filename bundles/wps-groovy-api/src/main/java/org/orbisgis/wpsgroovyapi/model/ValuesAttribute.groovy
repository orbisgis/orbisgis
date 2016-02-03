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

package org.orbisgis.wpsgroovyapi.model

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Groovy annotation that can be used in a groovy script to declare a value.
 * It can be a simple value (by default) or a range of values.
 *
 * @author Sylvain PALOMINOS
 */
@Retention(RetentionPolicy.RUNTIME)
@interface ValuesAttribute {

    /** Default value for the type attribute */
    ValuesType defaultType = ValuesType.VALUE
    /** Default value for the maximum attribute */
    String defaultMaximum = ""
    /** Default value for the minimum attribute */
    String defaultMinimum = ""
    /** Default value for the spacing attribute */
    String defaultSpacing = ""



    /** Only used if the type is VALUE Value represented */
    String value()
    /** Type of the value, it can be a simple value, or a range */
    ValuesType type() default ValuesType.VALUE
    /** Only used if the type is RANGE, indicates the range maximum */
    String maximum() default ""
    /** Only used if the type is RANGE, indicates the range minimum */
    String minimum() default ""
    /** Only used if the type is RANGE, indicates the spacing between two values. If not defined, there is no spacing */
    String spacing() default ""
}