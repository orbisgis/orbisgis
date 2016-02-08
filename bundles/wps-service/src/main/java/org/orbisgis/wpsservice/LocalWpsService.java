package org.orbisgis.wpsservice;

import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.model.DataType;
import org.orbisgis.wpsservice.model.Process;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.sql.*;
import java.util.*;

/**
 * @author Sylvain PALOMINOS
 */
public interface LocalWpsService extends WpsService {

    public void addLocalSource(URI uri, String iconName, boolean isDefaultScript);

    public List<ProcessIdentifier> getProcessIdentifierFromParent(URI parent);

    public ProcessIdentifier addLocalScript(File f, String iconName, boolean isDefaultScript);

    public void removeProcess(URI uri);

    public boolean checkProcess(URI uri);

    /**
     * Save a geocatalog table into a file.
     * @param uri URI where the table will be saved.
     * @param tableName Name of the table to save.
     */
    public void saveURI(URI uri, String tableName);


    /**
     * Verify if the given file is a well formed script.
     * @param uri URI to check.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkFolder(URI uri);


    /**
     * Returns a map of the importable format.
     * The map key is the format extension and the value is the format description.
     * @param onlySpatial If true, returns only the spatial table.
     * @return a map of the importable  format.
     */
    public static Map<String, String> getImportableFormat(boolean onlySpatial){
        Map<String, String> formatMap = new HashMap<>();
        for(DriverFunction df : driverFunctionContainer.getDriverFunctionList()){
            for(String ext : df.getImportFormats()){
                if(df.isSpatialFormat(ext) || !onlySpatial) {
                    formatMap.put(ext, df.getFormatDescription(ext));
                }
            }
        }
        return formatMap;
    }

    /**
     * Returns a map of the exportable spatial format.
     * The map key is the format extension and the value is the format description.
     * @param onlySpatial If true, returns only the spatial table.
     * @return a map of the exportable spatial format.
     */
    public static Map<String, String> getExportableFormat(boolean onlySpatial){
        Map<String, String> formatMap = new HashMap<>();
        for(DriverFunction df : driverFunctionContainer.getDriverFunctionList()){
            for(String ext : df.getExportFormats()){
                if(df.isSpatialFormat(ext) || !onlySpatial) {
                    formatMap.put(ext, df.getFormatDescription(ext));
                }
            }
        }
        return formatMap;
    }

    /**
     * Returns the list of sql table from OrbisGIS.
     * @param onlySpatial If true, returns only the spatial table.
     * @return The list of geo sql table from OrbisGIS.
     */
    public static List<String> getGeocatalogTableList(boolean onlySpatial) {
        List<String> list = new ArrayList<>();
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            String defaultSchema = "PUBLIC";
            try {
                if (connection.getSchema() != null) {
                    defaultSchema = connection.getSchema();
                }
            } catch (AbstractMethodError | Exception ex) {
                // Driver has been compiled with JAVA 6, or is not implemented
            }
            if(!onlySpatial) {
                DatabaseMetaData md = connection.getMetaData();
                ResultSet rs = md.getTables(null, defaultSchema, "%", null);
                while (rs.next()) {
                    String tableName = rs.getString(3);
                    if (!tableName.equalsIgnoreCase("SPATIAL_REF_SYS") && !tableName.equalsIgnoreCase("GEOMETRY_COLUMNS")) {
                        list.add(tableName);
                    }
                }
            }
            else{
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM "+defaultSchema+".geometry_columns");
                while(rs.next()) {
                    list.add(rs.getString("F_TABLE_NAME"));
                }
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error(e.getMessage());
        }
        return list;
    }

    /**
     * Return the list of the field of a table.
     * @param tableName Name of the table.
     * @param dataTypes Type of the field accepted. If empty, accepts all the field.
     * @return The list of the field name.
     */
    public static List<String> getTableFieldList(String tableName, List<DataType> dataTypes){
        List<String> fieldList = new ArrayList<>();
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet result = dmd.getColumns(connection.getCatalog(), null, tableName, "%");
            while(result.next()){
                if (!dataTypes.isEmpty()) {
                    for (DataType dataType : dataTypes) {
                        if (DataType.testHDBype(dataType, result.getObject(6).toString())) {
                            fieldList.add(result.getObject(4).toString());
                        }
                    }
                } else{
                    fieldList.add(result.getObject(4).toString());
                }
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error(e.getMessage());
        }
        return fieldList;
    }


    /**
     * Returns the list of distinct values contained by a field from a table from the database
     * @param tableName Name of the table containing the field.
     * @param fieldName Name of the field containing the values.
     * @return The list of distinct values of the field.
     */
    public static List<String> getFieldValueList(String tableName, String fieldName) {
        List<String> fieldValues = new ArrayList<>();
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT DISTINCT "+fieldName+" FROM "+tableName);
            while(result.next()){
                fieldValues.add(result.getString(1));
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error(e.getMessage());
        }
        return fieldValues;
    }

    /**
     * Removes a table from the database.
     * @param tableName Table to remove from the dataBase.
     */
    public static void removeTempTable(String tableName){
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            if(JDBCUtilities.tableExists(connection, tableName)) {
                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE " + tableName);
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error(e.getMessage());
        }
    }

    /**
     * Loads the given file into the geocatalog and return its table name.
     * @param uri URI to load.
     * @return Table name of the loaded file. Returns null if the file can't be loaded.
     */
    public String loadURI(URI uri, boolean copyInBase, Process p);

    public boolean isH2();
}
