package org.orbisgis.geometry_utils

import groovy.transform.Field
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Utility script used as extension module adding methods to Envelope class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

private static final @Field GeometryFactory FACTORY = new GeometryFactory()
private static final @Field Logger LOGGER = LoggerFactory.getLogger(this.class)

/**
 * Main AsType method allowing to convert Envelope into an other class.
 *
 * Supported classes : Polygon, Envelope
 * @param env Envelope to convert.
 * @param aClass Destination conversion class.
 * @return Instance of the given class from the Envelope.
 */
static Object asType(Envelope env, Class c) {
    switch(c) {
        case Polygon :
            return FACTORY.toGeometry(env)
        case Envelope :
            return env
    }
    return null
}