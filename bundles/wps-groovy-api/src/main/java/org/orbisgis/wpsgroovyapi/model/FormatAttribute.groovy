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

package org.orbisgis.wpsgroovyapi.model

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Attributes for the format of a data.
 * This annotation contains the needed attributes for a format.
 *
 * The following fields must be defined (mandatory) :
 *  - mimeType : String
 *      Media type of the data.
 *  - schema : String
 *      Identification of the data schema. Should be a valid URI.
 *
 * The following fields can be defined (optional) :
 *  - encoding : String
 *      Encoding procedure or character set of the data. Fixed to simple.
 *  - maximumMegaBytes : int
 *      The maximum size of the input data, in megabytes.
 *  - isDefaultFormat : boolean
 *      Indicates that this format is the default format. One of the FormatAttribute shall be the default one.
 *
 * @author Sylvain PALOMINOS
 */
@Retention(RetentionPolicy.RUNTIME)
@interface FormatAttribute {
    /** Media type of the data. */
    String mimeType()
    /** Encoding procedure or character set of the data. Fixed to simple.*/
    String encoding() default "simple"
    /** Identification of the data schema.*/
    String schema()
    /** The maximum size of the input data, in megabytes.*/
    int maximumMegaBytes() default 0
    /** Indicates that this format is the default format. One of the FormatAttribute shall be the default one.*/
    boolean isDefaultFormat() default false
}