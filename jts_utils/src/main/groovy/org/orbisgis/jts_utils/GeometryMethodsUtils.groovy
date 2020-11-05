package org.orbisgis.groovy_utils

import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry

/**
 * Utility script used as extension module adding methods JTS Geometry.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/**
 * Return the expanded envelope of the given geometry with the given meters.
 *
 * @param geom Geometry which envelope is expanded.
 * @param distance Distance in meter for the expansion
 * @return The expanded envelope.
 */
static Envelope expandEnvelopeByMeters(Geometry geom, int distance) {
    return geom ? geom.getEnvelopeInternal().expandEnvelopeByMeters(distance) : null
}
