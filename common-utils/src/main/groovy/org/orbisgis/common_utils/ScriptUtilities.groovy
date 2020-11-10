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

import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Utility script used as extension module adding methods to Script class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

@Field static final Map<Class, Logger> loggerMap = new HashMap<>()

/**
 * Return the cached logger corresponding to the given class.
 *
 * @param c Class which logger should be get.
 * @return The class logger.
 */
private static final Logger getLogger(Class c){
    if(!loggerMap.containsKey(c)) {
        loggerMap.put(c, LoggerFactory.getLogger(c))
    }
    return loggerMap.get(c)
}

static void info(Script script, String message) {
    getLogger(script.getClass()).info(message)
}

static void info(Script script, GString message) {
    getLogger(script.getClass()).info(message)
}

static void info(Script script, Object obj) {
    getLogger(script.getClass()).info(obj.toString())
}

static void info(Script script, String message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void info(Script script, GString message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void info(Script script, Object obj, Throwable t) {
    getLogger(script.getClass()).info(obj.toString(), t)
}

static void warn(Script script, String message) {
    getLogger(script.getClass()).info(message)
}

static void warn(Script script, GString message) {
    getLogger(script.getClass()).info(message)
}

static void warn(Script script, Object obj) {
    getLogger(script.getClass()).info(obj.toString())
}

static void warn(Script script, String message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void warn(Script script, GString message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void warn(Script script, Object obj, Throwable t) {
    getLogger(script.getClass()).info(obj.toString(), t)
}

static void error(Script script, String message) {
    getLogger(script.getClass()).info(message)
}

static void error(Script script, GString message) {
    getLogger(script.getClass()).info(message)
}

static void error(Script script, Object obj) {
    getLogger(script.getClass()).info(obj.toString())
}

static void error(Script script, String message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void error(Script script, GString message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void error(Script script, Object obj, Throwable t) {
    getLogger(script.getClass()).info(obj.toString(), t)
}

static void debug(Script script, String message) {
    getLogger(script.getClass()).info(message)
}

static void debug(Script script, GString message) {
    getLogger(script.getClass()).info(message)
}

static void debug(Script script, Object obj) {
    getLogger(script.getClass()).info(obj.toString())
}

static void debug(Script script, String message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void debug(Script script, GString message, Throwable t) {
    getLogger(script.getClass()).info(message, t)
}

static void debug(Script script, Object obj, Throwable t) {
    getLogger(script.getClass()).info(obj.toString(), t)
}

/** {@link Closure} returning a {@link String} prefix/suffix build from a random {@link UUID} with '-' replaced by '_'. */
static String getUuid(Script unused) {
    UUID.randomUUID().toString().replaceAll("-", "_")
}
