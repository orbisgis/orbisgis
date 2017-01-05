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
 * Attributes for the literal data.
 * This annotation contains the needed attributes for a literal data.
 * The LiteralData represents a number or a string.
 *
 *
 * The following fields can be defined (optional) :
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
 * @author Sylvain PALOMINOS
 */
@Retention(RetentionPolicy.RUNTIME)
@interface LiteralDataAttribute {

    /**
     * This attribute contains the definition of the default possible value domain. The domains can be simple values
     * ("foo", 2, 2.6589 ...) or value ranges with min, max and sometimes spacing (1;;42, 0;0.1;1 ...). If there is no
     * default domain, all the values are accepted.
     *
     * The possible value choices can have three different patterns :
     *  - value : A string representation of a possible value.
     *  - min;;max : A range of valid values with a min value and a max value.
     *  - min;spacing;max : A range of valid value with a spacing between two value, a min, and a max.
     *  i.e. :
     *  defaultDomain = "0;;1, 1;1;100, 1000"
     *  The allowed values are a value between 0 and 1, a value between 1 and 100 with a spacing of 1 and the value 1000.
     */
    String defaultDomain() default ""

    /**
     * This attribute contains the definition of all the possible value domain. The domains can be simple values
     * ("foo", 2, 2.6589 ...) or value ranges with min, max and sometimes spacing (1;;42, 0;0.1;1 ...). If there is no
     * default domain, the domains should be ignored.
     *
     * This attribute is composed of a list of coma separated values. The values are the possible value choices which
     * are define later. The very first value will be the default one. The possible value choices can have three
     * different patterns :
     *  - value : A string representation of a possible value.
     *  - min;;max : A range of valid values with a min value and a max value.
     *  - min;spacing;max : A range of valid value with a spacing between two value, a min, and a max.
     *  i.e.
     *  validDomains = ["value1,value2,value3","0;;1,1;1;100"]
     *  The allowed values are value1 (the default one), value2, value3 or a value between 0 and 1, a value between 1
     *  and 100 with a spacing of 1.
     */
    String[] validDomains() default []
}