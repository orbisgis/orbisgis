/*
 * Bundle OSM is part of the OrbisGIS platform
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
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.osm_utils.overpass

import java.text.SimpleDateFormat
import java.time.ZoneId

/**
 * Locked slot of the Overpass server which is free after a wait time.
 *
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
class Slot {

    /** String used to parse the slot {@link java.lang.String} representation */
    private static final SLOT_AVAILABLE_AFTER = "Slot available after: "
    /** String used to parse the slot {@link java.lang.String} representation */
    private static final IN = ", in "
    /** String used to parse the slot {@link java.lang.String} representation */
    private static final SECONDS = " seconds."

    /** {@link java.text.SimpleDateFormat} used to parse dates. */
    private format = "yyyy-MM-dd'T'HH:mm:ss'Z'" as SimpleDateFormat
    private local = "yyyy-MM-dd'T'HH:mm:ss'Z'" as SimpleDateFormat

    /** {@link java.util.Date} when the slot will be available. */
    def availability
    /** Time in seconds to wait until the slot is available.*/
    def waitSeconds

    /**
     * Main constructor.
     * @param text {@link java.lang.String} representation of the slot.
     */
    Slot(String text){
        format.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"))
        local.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        def values = (text - SLOT_AVAILABLE_AFTER - SECONDS).split(IN)
        availability = format.parse(values[0])
        waitSeconds = Long.decode(values[1])
    }

    @Override
    String toString(){
        return "$SLOT_AVAILABLE_AFTER${local.format(availability)}$IN$waitSeconds$SECONDS"
    }
}
