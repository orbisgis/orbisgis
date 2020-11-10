/*
 * Bundle common-utils is part of the OrbisGIS platform
 *
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
 * OSM is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OSM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OSM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OSM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.common_utils

import java.util.regex.Pattern

import static java.nio.charset.StandardCharsets.UTF_8

/**
 * Utility script used as extension module adding methods to String class.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTARE 2020)
 */

/**
 * Prefix the given string with an {@link UUID}.
 *
 * @param self The string to prefix.
 * @return The prefixed string.
 */
static String prefix(String self) {
    return UUID.randomUUID().toString().replaceAll("-", "_") + "_" + self
}

/**
 * Prefix the given string with an {@link UUID}.
 *
 * @param self   The string to prefix.
 * @param prefix The prefix to apply.
 * @return The prefixed string.
 */
static String prefix(String self, String prefix) {
    return prefix + "_" + self
}

/**
 * Postfix the given string with an {@link UUID}.
 *
 * @param self The string to postfix.
 * @return The postfixed string.
 */
static String postfix(String self) {
    return self + "_" + UUID.randomUUID().toString().replaceAll("-", "_")
}

/**
 * Postfix the given string with an {@link UUID}.
 *
 * @param self   The string to postfix.
 * @param postfix The postfix to apply.
 * @return The postfixed string.
 */
static String postfix(String self, String postfix) {
    return self + "_" + postfix
}

/**
 * Compile the given string as a regex Pattern.
 *
 * @param regex String regex to compile.
 * @return The regex Pattern.
 */
static Pattern compileRegex(String regex) {
    return Pattern.compile(regex);
}

/**
 * Convert an UTF-8 {@link String} into an {@link URL}.
 */
static String utf8ToUrl(String str){
    return URLEncoder.encode(str, UTF_8.toString())
}
