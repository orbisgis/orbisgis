/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE
 * team located in University of South Brittany, Vannes.
 *
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.wpsservice;

import net.opengis.ows._2.CodeType;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.model.DataType;

import java.io.*;
import java.util.*;

/**
 * @author Sylvain PALOMINOS
 */
public interface LocalWpsService extends WpsService {

    String TABLE_SRID = "TABLE_SRID";
    String TABLE_IS_SPATIAL = "TABLE_IS_SPATIAL";
    String TABLE_DIMENSION = "TABLE_DIMENSION";
    String GEOMETRY_TYPE = "GEOMETRY_TYPE";
    String TABLE_LOCATION = "TABLE_LOCATION";
    String TABLE_LABEL = "TABLE_LABEL";

    /**
     * Add a local groovy file or directory of processes to the wps service.
     * @param f  File object to add to the service.
     * @param iconName Icon file name associated to the script
     * @param isDefaultScript True if the scripts are default scripts (unremovable). False otherwise
     */
    List<ProcessIdentifier> addLocalSource(File f, String[] iconName, boolean isDefaultScript, String nodePath);

    /**
     * Remove the process corresponding to the given codeType.
     * @param identifier CodeType identifier of the process.
     */
    void removeProcess(CodeType identifier);

    /**
     * Verify if the process corresponding to the identifier is a valid and well formed groovy wps script.
     * @param identifier CodeType identifier of the process to check.
     * @return True if the script is valid, false otherwise.
     */
    boolean checkProcess(CodeType identifier);

    /**
     * Returns a map containing the sql table from OrbisGIS as key and if it is spatial or not as value.
     * @param onlySpatial If true, returns only the spatial table.
     * @return A map containing the sql table from OrbisGIS as key and if it is spatial or not as value.
     */
    Map<String, Boolean> getGeocatalogTableList(boolean onlySpatial);

    /**
     * Returns a map containing field information (table type, SRID, ...)
     * @param tableName Name of the table.
     * @param fieldName Name of the field.
     * @return Map containing the field information.
     */
    Map<String, Object> getFieldInformation(String tableName, String fieldName);

    /**
     * Return the list of the field of a table.
     * @param tableName Name of the table.
     * @param dataTypes Type of the field accepted. If empty, accepts all the field.
     * @return The list of the field name.
     */
    List<String> getTableFieldList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes);


    /**
     * Returns the list of distinct values contained by a field from a table from the database
     * @param tableName Name of the table containing the field.
     * @param fieldName Name of the field containing the values.
     * @return The list of distinct values of the field.
     */
    List<String> getFieldValueList(String tableName, String fieldName);

    /**
     * Returns true if the data base is H2, false otherwise.
     * @return True if the data base is H2, false otherwise.
     */
    boolean isH2();

    /**
     * Cancel the running process corresponding to the given URI.
     * @param jobId Id of the job to cancel.
     */
    void cancelProcess(UUID jobId);

    enum ProcessProperty{IS_REMOVABLE, NODE_PATH, ICON_ARRAY, ROLE, DBMS}
}
