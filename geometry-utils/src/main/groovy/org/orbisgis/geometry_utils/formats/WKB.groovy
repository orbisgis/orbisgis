/*
 * Bundle Geomerty_Utils is part of the OrbisGIS platform
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
package org.orbisgis.geometry_utils.formats

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKBWriter

/**
 * WKB geometry format used to do Geometry as WKT.
 * As the asType impose to return an Object of the given class, 'geom as WKB' have to return a WKB Object, so
 * WKB class extends LinkedList<Byte> to be used like an Array.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class WKB extends LinkedList<Byte> {

    /** WKB writer used to convert Geometry to WKB byte array. */
    private static final WKBWriter WKB_WRITER = new WKBWriter(2, 2)

    /** Main constructor. */
    WKB(Geometry geom) {
        super()
        def array = WKB_WRITER.write(geom)
        Byte[] objects = new Byte[array.length]
        for(int i=0; i<array.length; i++) {
            objects[i] = new Byte(array[i])
        }
        addAll(objects)
    }
}