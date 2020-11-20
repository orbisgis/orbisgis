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

import java.sql.Connection

/**
 * Script containing utility methods for the {@link org.orbisgis.osm_utils.Extract} script to keep only
 * main methods inside the groovy script.
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */

/**
 * Return SQL query which select all the given OSM tags
 *
 * @param osmTableTag   Name of the table of OSM tag.
 * @param filters       List of OSM tags keys used to filter the OSM elements.
 * @param columnsToKeep List of columns to keep.
 *
 * @return The SQL column selection query.
 */
static def getColumnSelectorQuery(def osmTableTag, def filters, def columnsToKeep) {
    if (!osmTableTag) {
        error "The table name should not be empty or null."
        return null
    }
    def tagKeys = []
    if (filters != null) {
        def tagKeysList = filters in Map ? filters.keySet() : filters
        tagKeys.addAll(tagKeysList.findResults { it && it != "null" ? it : null })
    }
    if (columnsToKeep != null) {
        tagKeys.addAll(columnsToKeep)
    }
    tagKeys.removeAll([null])

    def query = "SELECT distinct tag_key FROM $osmTableTag"
    if (tagKeys) query += " WHERE tag_key IN ('${tagKeys.unique().join("','")}')"
    return query
}

/**
 * Return the tag count query
 *
 * @param osmTableTag   Name of the table of OSM tag.
 * @param tags          List of keys and values to be filtered.
 * @param columnsToKeep List of columns to keep.
 *
 * @return The tag count query
 */
static def getCountTagsQuery(def osmTableTag, def tags) {
    if(!osmTableTag) return null
    def countTagsQuery = "SELECT count(*) AS count FROM $osmTableTag"
    def whereFilter = createWhereFilter(tags)
    if (whereFilter) {
        countTagsQuery += " WHERE $whereFilter"
    }
    return countTagsQuery
}

/**
 * Method to build a where filter based on a list of key, values
 *
 * @param tags The input Map of key and values with the following signature
 * ["building", "landcover"] or
 * ["building": ["yes"], "landcover":["grass", "forest"]]
 * @return a where filter as
 * tag_key in '(building', 'landcover') or
 * (tag_key = 'building' and tag_value in ('yes')) or (tag_key = 'landcover' and tag_value in ('grass','forest')))
 */
static def createWhereFilter(def tags){
    if(!tags){
        warn "The tag map is empty"
        return ""
    }
    def whereKeysValuesFilter = ""
    if(tags in Map){
        def whereQuery = []
        tags.each{ tag ->
            def keyIn = ''
            def valueIn = ''
            if(tag.key){
                if(tag.key instanceof Collection) {
                    keyIn += "tag_key IN ('${tag.key.join("','")}')"
                }
                else {
                    keyIn += "tag_key = '${tag.key}'"
                }
            }
            if(tag.value){
                def valueList = (tag.value instanceof Collection) ? tag.value.flatten().findResults{it} : [tag.value]
                valueIn += "tag_value IN ('${valueList.join("','")}')"
            }

            if(!keyIn.isEmpty()&& !valueIn.isEmpty()){
                whereQuery+= "$keyIn AND $valueIn"
            }
            else if(!keyIn.isEmpty()){
                whereQuery+= "$keyIn"
            }
            else if(!valueIn.isEmpty()){
                whereQuery+= "$valueIn"
            }
        }
        whereKeysValuesFilter = "(${whereQuery.join(') OR (')})"
    }
    else {
        def tagArray = []
        tagArray.addAll(tags)
        tagArray.removeAll([null])
        if(tagArray) {
            whereKeysValuesFilter = "tag_key IN ('${tagArray.join("','")}')"
        }
    }
    return whereKeysValuesFilter
}

/**
 * Build a case when expression to pivot keys
 *
 * @param connection       Connection to a database.
 * @param selectTableQuery The table that contains the keys and values to pivot.
 *
 * @return The SQL case when expression.
 */
static def createTagList(Connection connection, def selectTableQuery){
    if (!connection) {
        error "Invalid database connection"
        return
    }
    def rowskeys = connection.rows(selectTableQuery)
    def list = []
    rowskeys.tag_key
            .findAll {it != null}
            .each { list << "MAX(CASE WHEN b.tag_key = '$it' THEN b.tag_value END) AS \"${it.toUpperCase()}\""}
    def tagList =""
    if (list) {
        tagList = ", ${list.join(",")}"
    }
    return tagList
}