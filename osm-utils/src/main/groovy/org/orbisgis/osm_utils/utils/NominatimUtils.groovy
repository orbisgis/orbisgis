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
package org.orbisgis.osm_utils.utils

import groovy.json.JsonSlurper
import groovy.transform.Field
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon

import static java.net.Proxy.NO_PROXY
import static java.net.Proxy.Type.HTTP

/**
 * Script containing utility methods for the Nominatim queries.
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */

/** Get method for HTTP request. */
@Field static final String GET = "GET"
/** Nominatim server base URL. */
@Field static final String NOMINATIM_BASE_URL =
        System.getProperty("NOMINATIM_ENPOINT")?:"https://nominatim.openstreetmap.org" + "/search?q="
/** Url of the status of the Overpass server. */
@Field static final String NOMINATIM_END_URL = "&limit=5&format=geojson&polygon_geojson=1"
/** Success code. */
@Field static final int SUCCESS_CODE = 200
/** Default http port. */
@Field static final String DEFAULT_HTTP_PORT = "80"

/**
 * Method to execute an Nominatim query and save the result in a file.
 *
 * @param query         The Nominatim query.
 * @param outputOSMFile The output file.
 *
 * @return True if the file has been downloaded, false otherwise.
 *
 */
static boolean executeNominatimQuery(def query, def outputOSMFile) {
    if (!query) {
        error "The Nominatim query should not be null."
        return false
    }
    if (!(outputOSMFile instanceof File)) {
        error "The OSM file should be an instance of File"
        return false
    }

    URL url = new URL(NOMINATIM_BASE_URL + query.utf8ToUrl() + NOMINATIM_END_URL)

    def proxyHost = System.getProperty("http.proxyHost")
    def proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", DEFAULT_HTTP_PORT))
    def proxy = proxyHost != null ? new Proxy(HTTP, new InetSocketAddress(proxyHost, proxyPort)) : NO_PROXY
    def connection = url.openConnection(proxy) as HttpURLConnection

    connection.requestMethod = GET
    connection.connect()

    info url
    info "Executing query... $query"
    //Save the result in a file
    if (connection.responseCode == SUCCESS_CODE) {
        info "Downloading the Nominatim data."
        outputOSMFile << connection.inputStream
        return true
    } else {
        error "Cannot execute the Nominatim query."
        return false
    }
}

/**
 * Return the area of a city name as a geometry.
 *
 * @param placeName The Nominatim place name.
 *
 * @return A {@link org.locationtech.jts.geom.Geometry} representing the area of the given place.
 */
static Geometry getArea(String placeName) {
    if (!placeName) {
        error "The place name should not be null or empty."
        return
    }
    def outputOSMFile = File.createTempFile("nominatim_osm", ".geojson")
    if (!executeNominatimQuery(placeName, outputOSMFile)) {
        if (!outputOSMFile.delete()) {
            warn "Unable to delete the file '$outputOSMFile'."
        }
        warn "Unable to execute the Nominatim query."
        return
    }

    def jsonRoot = new JsonSlurper().parse(outputOSMFile)
    if (!jsonRoot) {
        error "Cannot find any data from the place $placeName."
        return
    }

    if (jsonRoot.features.size() == 0) {
        error "Cannot find any features from the place $placeName."
        if (!outputOSMFile.delete()) {
            warn "Unable to delete the file '$outputOSMFile'."
        }
        return
    }

    def area = null
    jsonRoot.features.find() { feature ->
        if (feature.geometry != null) {
            if (feature.geometry.type.equalsIgnoreCase("polygon")) {
                area = feature.geometry.coordinates as Polygon
            } else if (feature.geometry.type.equalsIgnoreCase("multipolygon")) {
                area = feature.geometry.coordinates.collect { it as Polygon }.toArray() as MultiPolygon
            } else {
                return
            }
            area.setSRID(4326)
            return
        }
        return
    }
    if (!outputOSMFile.delete()) {
        warn "Unable to delete the file '$outputOSMFile'."
    }
    return area
}

/**
 * This method is used to build a geometry following the Nominatim bbox signature.
 * Nominatim API returns a boundingbox property of the form:
 * south Latitude, west Longitude, north Latitude, east Longitude
 *  south : float -> southern latitude of bounding box
 *  west : float  -> western longitude of bounding box
 *  north : float -> northern latitude of bounding box
 *  east : float  -> eastern longitude of bounding box
 *
 * @param bbox List of 4 float values.
 *
 * @return A JTS polygon.
 */
static Geometry geometryFromNominatim(Collection<Float> bbox) {
    def env = bbox as Envelope
    if (env) {
        def poly = env as Polygon
        poly.SRID = 4326
        return poly.valid ? poly : null
    }
}