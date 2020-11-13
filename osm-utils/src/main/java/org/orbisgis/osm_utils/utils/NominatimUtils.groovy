package org.orbisgis.osm_utils.utils

import groovy.json.JsonSlurper
import groovy.transform.Field
import org.cts.util.UTMUtils
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
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
        System.getProperty("NOMINATIM_ENPOINT")?:"http://nominatim.openstreetmap.org" + "/search?q="
/** Url of the status of the Overpass server. */
@Field static final String NOMINATIM_END_URL = "&limit=5&format=geojson&polygon_geojson=1"
/** Success code. */
@Field static final int SUCCESS_CODE = 200

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
    def proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "80"))
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

    GeometryFactory geometryFactory = new GeometryFactory()

    def area = null
    jsonRoot.features.find() { feature ->
        if (feature.geometry != null) {
            if (feature.geometry.type.equalsIgnoreCase("polygon")) {
                area = feature.geometry.coordinates as Polygon
            } else if (feature.geometry.type.equalsIgnoreCase("multipolygon")) {
                def mp = feature.geometry.coordinates.collect { it as Polygon }.toArray(new Polygon[0])
                area = geometryFactory.createMultiPolygon(mp)
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
 * south Latitude, north Latitude, west Longitude, east Longitude
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
    if (!bbox) {
        error "The latitude and longitude values cannot be null or empty"
        return null
    }
    if (!(bbox instanceof Collection) && !bbox.class.isArray()) {
        error "The latitude and longitude values must be set as an array"
        return null
    }
    if (bbox.size() != 4) {
        error("The bbox must be defined with 4 values")
        return null
    }
    def minLong = bbox[0], minLat = bbox[1]
    def maxLong = bbox[2], maxLat = bbox[3]
    if (UTMUtils.isValidLatitude(minLat) && UTMUtils.isValidLatitude(maxLat)
            && UTMUtils.isValidLongitude(minLong) && UTMUtils.isValidLongitude(maxLong)) {
        def geom = new Envelope(minLong, maxLong, minLat, maxLat) as Polygon
        geom.setSRID(4326)
        return geom.isValid() ? geom : null
    }
    error("Invalid latitude/longitude values")
    return null
}