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

import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.model.DataType;

import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * @author Sylvain PALOMINOS
 */
public interface LocalWpsService extends WpsService {

    String TABLE_SRID = "TABLE_SRID";
    String TABLE_IS_SPATIAL = "TABLE_IS_SPATIAL";
    String TABLE_DIMENSION = "TABLE_DIMENSION";
    String GEOMETRY_TYPE = "GEOMETRY_TYPE";

    void addLocalSource(URI uri, String iconName, boolean isDefaultScript);

    List<ProcessIdentifier> getProcessIdentifierFromParent(URI parent);

    ProcessIdentifier addLocalScript(File f, String iconName, boolean isDefaultScript);

    void removeProcess(URI uri);

    boolean checkProcess(URI uri);

    /**
     * Save a geocatalog table into a file.
     * @param uri URI where the table will be saved.
     * @param tableName Name of the table to save.
     */
    void saveURI(URI uri, String tableName);


    /**
     * Verify if the given file is a well formed script.
     * @param uri URI to check.
     * @return True if the file is well formed, false otherwise.
     */
    boolean checkFolder(URI uri);


    /**
     * Returns a map of the importable format.
     * The map key is the format extension and the value is the format description.
     * @param onlySpatial If true, returns only the spatial table.
     * @return a map of the importable  format.
     */
    Map<String, String> getImportableFormat(boolean onlySpatial);

    /**
     * Returns a map of the exportable spatial format.
     * The map key is the format extension and the value is the format description.
     * @param onlySpatial If true, returns only the spatial table.
     * @return a map of the exportable spatial format.
     */
    Map<String, String> getExportableFormat(boolean onlySpatial);

    /**
     * Removes a table from the database.
     * @param tableName Table to remove from the dataBase.
     */
    void removeTempTable(String tableName);

    /**
     * Returns the list of sql table from OrbisGIS.
     * @param onlySpatial If true, returns only the spatial table.
     * @return The list of geo sql table from OrbisGIS.
     */
    List<String> getGeocatalogTableList(boolean onlySpatial);

    /**
     * Returns a map containing table information (table type, SRID, ...)
     * @param tableName Name of the table.
     * @return Map containing the table information.
     */
    Map<String, Object> getTableInformation(String tableName);

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
     * Loads the given file into the geocatalog and return its table name.
     * @param uri URI to load.
     * @return Table name of the loaded file. Returns null if the file can't be loaded.
     */
    String loadURI(URI uri, boolean copyInBase);

    /**
     * Returns true if the data base is H2, false otherwise.
     * @return True if the data base is H2, false otherwise.
     */
    boolean isH2();

    /**
     * Cancel the running process corresponding to the given URI.
     * @param uri URI of the process to cancel.
     */
    void cancelProcess(URI uri);
}
