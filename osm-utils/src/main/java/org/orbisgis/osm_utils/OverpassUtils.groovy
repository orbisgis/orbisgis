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
package org.orbisgis.osm_utils

import groovy.transform.Field
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Polygon
import org.orbisgis.osm_utils.overpass.OSMElement
import org.orbisgis.osm_utils.overpass.OverpassStatus

import static java.net.Proxy.NO_PROXY
import static java.net.Proxy.Type.HTTP

/**
 * Script containing utility methods for the OverPass queries.
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */

/** Get method for HTTP request. */
@Field static final String GET = "GET"
/** URL end point. */
@Field static final String END_POINT = System.getProperty("OVERPASS_ENPOINT")?:"http://overpass-api.de/api"
/** Overpass server base URL. */
@Field static final String OVERPASS_BASE_URL = OverpassUtils.END_POINT + "/interpreter?data="
/** Url of the status of the Overpass server. */
@Field static final String OVERPASS_STATUS_URL = OverpassUtils.END_POINT + "/status"
/** Success code. */
@Field static final int SUCCESS_CODE = 200


/**
 * This process extracts OSM data as an XML file using the Overpass API
 *
 * @param overpassQuery The overpass api to be executed
 *
 * @return The osm file path.
 */
static def extract(String overpassQuery) {
    info "Extract the OSM data"
    if(!overpassQuery){
        error "The query should not be null or empty."
        return
    }
    def queryHash = overpassQuery.utf8ToUrl().digest('SHA-256')
    def outputOSMFile = new File(System.getProperty("java.io.tmpdir"), "${queryHash}.osm")
    def osmFilePath = outputOSMFile.absolutePath
    if(outputOSMFile.exists()){
        if(outputOSMFile.length()==0){
            if(!outputOSMFile.delete()){
                error "Cannot delete the old overpass query result file."
                return
            }
        }
        else {
            info "\nThe cached OSM file ${osmFilePath} will be re-used for the query :\n$overpassQuery."
            return osmFilePath
        }
    }
    if (!outputOSMFile.createNewFile()) {
        error "Cannot create the overpass query result file."
        return
    }
    if (executeAsOverPassQuery(overpassQuery, outputOSMFile)) {
        info "The OSM file has been downloaded at ${osmFilePath}."
        return osmFilePath
    } else {
        error "Cannot extract the OSM data for the query $overpassQuery."
    }
}

/**
 * Return the status of the Overpass server.
 *
 * @return A {@link org.orbisgis.osm_utils.overpass.OverpassStatus} instance.
 */
static def getServerStatus()  {

    def proxyHost = System.getProperty("http.proxyHost")
    def proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "80"))
    def proxy = proxyHost != null ? new Proxy(HTTP, new InetSocketAddress(proxyHost, proxyPort)) : NO_PROXY
    def connection = new URL(OVERPASS_STATUS_URL).openConnection(proxy) as HttpURLConnection

    connection.requestMethod = GET
    connection.connect()

    if (connection.responseCode == SUCCESS_CODE) {
        def content = connection.inputStream.text
        return new OverpassStatus(content)
    } else {
        error "Cannot get the status of the server.\n Server answer with code ${connection.responseCode} : " +
                "${connection.inputStream.text}"
    }
}

/**
 * Wait for a free overpass slot.
 *
 * @param timeout Timeout to limit the waiting.
 *
 * @return True if there is a free slot, false otherwise.
 */
static def wait(int timeout)  {
    def to = timeout
    def status = getServerStatus()
    info("Try to wait for slot available")
    if(!status.waitForSlot(timeout)){
        //Case of too low timeout for slot availability
        if(status.slotWaitTime > to){
            error("Wait timeout is lower than the wait time for a slot.")
            return false
        }
        info("Wait for query end")
        if(!status.waitForQueryEnd(to)){
            error("Wait timeout is lower than the wait time for a query end.")
            return false
        }
        to -= status.queryWaitTime
        //Case of too low timeout for slot availability
        if(status.slotWaitTime > to){
            error("Wait timeout is lower than the wait time for a slot.")
            return false
        }
        info("Wait for slot available")
        return status.waitForSlot(timeout)
    }
    return true
}

/**
 * Method to execute an Overpass query and save the result in a file.
 *
 * @param query         URL, String or GString of the Overpass query.
 * @param outputOSMFile Output file where the OSM data wil be written.
 *
 * @return True if the query has been successfully executed, false otherwise.
 */
static boolean executeAsOverPassQuery(def query, File outputOSMFile) {
    if(!query){
        error "The query should not be null or empty."
        return false
    }
    if(!outputOSMFile){
        error "The output file should not be null or empty."
        return false
    }
    def queryUrl
    if(query instanceof URL) {
        queryUrl = query
    }
    else if(query instanceof String || query instanceof GString) {
        queryUrl = new URL(OVERPASS_BASE_URL + query.toString().utf8ToUrl())
    }
    else {
        error "The query should be an URL or a String or a GString."
        return false
    }
    info queryUrl

    def proxyHost = System.getProperty("http.proxyHost")
    def proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "80"))
    def proxy = proxyHost != null ? new Proxy(HTTP, new InetSocketAddress(proxyHost, proxyPort)) : NO_PROXY
    def connection = queryUrl.openConnection(proxy) as HttpURLConnection

    connection.requestMethod = GET
    connection.connect()

    info "Executing query... $query"
    //Save the result in a file
    if (connection.responseCode == SUCCESS_CODE) {
        info "Downloading the OSM data from overpass api in ${outputOSMFile}"
        outputOSMFile << connection.inputStream
        return true
    }
    error "Cannot execute the query.\n${getServerStatus()}"
    return false
}

/**
 * Extract the OSM bbox signature from a Geometry.
 * e.g. (bbox:"50.7 7.1 50.7 7.12 50.71 7.11")
 *
 * @param geometry Input geometry.
 *
 * @return OSM bbox.
 */
static String toBBox(Geometry geometry) {
    if (!geometry) {
        error "Cannot convert to an overpass bounding box."
        return null
    }
    def env = geometry.getEnvelopeInternal()
    return "(bbox:${env.getMinY()},${env.getMinX()},${env.getMaxY()},${env.getMaxX()})".toString()
}

/**
 * This method is used to build a geometry following the overpass bbox signature.
 * The order of values in the bounding box used by Overpass API is :
 * south ,west, north, east
 *
 *  south : float -> southern latitude of bounding box
 *  west : float  -> western longitude of bounding box
 *  north : float -> northern latitude of bounding box
 *  east : float  -> eastern longitude of bounding box
 *
 *  So : minimum latitude, minimum longitude, maximum latitude, maximum longitude
 *
 * @param bbox List of 4 float values.
 *
 * @return A JTS polygon
 */
static Geometry geometryFromOverpass(Collection<Collection> bbox) {
    if (!bbox) {
        error "The latitude and longitude values cannot be null or empty"
        return
    }
    if (!(bbox instanceof Collection)) {
        error "The latitude and longitude values must be set as an array"
        return
    }
    if (bbox.size() == 4) {
        return [bbox[1], bbox[0], bbox[3], bbox[2]] as Polygon
    }
    error("The bbox must be defined with 4 values")
}

/**
 * Method to build a valid and optimized OSM query
 *
 * @param polygon    The polygon to filter.
 * @param keys       List of OSM keys.
 * @param osmElement List of OSM elements to build the query (node, way, relation).
 *
 * @return A string representation of the OSM query.
 */
static String buildOSMQuery(def geomOrEnvelope, def keys, def osmElement, def getAllData = true) {
    if (geomOrEnvelope == null) {
        error "Cannot create the overpass query from a null polygon/envelope."
        return null
    }
    if (!geomOrEnvelope instanceof Geometry && !geomOrEnvelope instanceof Polygon) {
        error "Cannot create the overpass query from an other object than a polygon/envelope."
        return null
    }
    def geom = geomOrEnvelope
    if(geom instanceof Envelope) {
        geom = geom as Polygon
    }
    if (geom.isEmpty()) {
        error "Cannot create the overpass query from an empty polygon."
        return null
    }
    def envelope = geom.getEnvelopeInternal()
    def filterArea = toOSMPoly(geom)
    def filters = "";
    //Write the bbox filtering
    def query = "[bbox:${envelope.getMinY()},${envelope.getMinX()},${envelope.getMaxY()},${envelope.getMaxX()}];\n(\n"
    //Write the filters from the keys. If no keys, the filters is empty
    if(keys && keys instanceof Collection) {
        keys.each {
            filters += "[\"${it.toString().toLowerCase()}\"]"
        }
    }
    //For each OSMElements add the corresponding query part with the filters if there is keys and filterArea if all data should be get.
    osmElement.each { i ->
        query += "\t${i.name().toLowerCase()}$filters${getAllData?filterArea:""};\n"
    }
    //Close the query
    if (!filters) {
        query += ");\nout;"
    } else {
        query += ");\n(._;>;);\nout;"
    }

    return query
}

/**
 * Extract the OSM poly signature from a Geometry
 * e.g. (poly:"50.7 7.1 50.7 7.12 50.71 7.11")
 *
 * @param geometry Input geometry.
 *
 * @return The OSM polygon.
 */
static String toOSMPoly(Geometry geometry) {
    if (!geometry) {
        error "Cannot convert to an overpass poly filter."
        return null
    }
    if (!(geometry instanceof Polygon)) {
        error "The input geometry must be polygon."
        return null
    }
    if (geometry.isEmpty()) {
        error "The input geometry must be polygon."
        return null
    }
    def polyStr = "(poly:\""
    geometry.getExteriorRing().getCoordinates()[0..<-1].each {
        polyStr += "${it.getY()} ${it.getX()} "
    }
    return polyStr[0..<-1] + "\")"
}