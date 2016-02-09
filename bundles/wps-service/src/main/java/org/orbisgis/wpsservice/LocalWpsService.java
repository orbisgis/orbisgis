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
     * Return the list of the field of a table.
     * @param tableName Name of the table.
     * @param dataTypes Type of the field accepted. If empty, accepts all the field.
     * @return The list of the field name.
     */
    List<String> getTableFieldList(String tableName, List<DataType> dataTypes);


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

    boolean isH2();
}
