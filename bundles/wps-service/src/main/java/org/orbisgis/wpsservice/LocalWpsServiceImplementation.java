/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.h2spatialapi.EmptyProgressVisitor;
import org.h2gis.h2spatialapi.ProgressVisitor;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wpsservice.controller.execution.DataProcessingManager;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.controller.process.ProcessManager;
import org.orbisgis.wpsservice.model.DataType;
import org.orbisgis.wpsservice.model.DescriptionType;
import org.orbisgis.wpsservice.model.Process;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.*;

@Component(service = {LocalWpsService.class})
public class LocalWpsServiceImplementation implements LocalWpsService {
    /** String of the Groovy file extension. */
    public static final String GROOVY_EXTENSION = "groovy";
    private static final String WPS_SCRIPT_FOLDER = "Scripts";
    private static final String TOOLBOX_PROPERTIES = "toolbox.properties";
    private static final String PROPERTY_SOURCES = "PROPERTY_SOURCES";
    private static final String[] SHOWN_TABLE_TYPES =
            new String[]{"TABLE", "SYSTEM TABLE","LINKED TABLE","VIEW", "EXTERNAL"};

    private CoreWorkspace coreWorkspace;
    boolean multiThreaded;
    /** True if the database is H2, false otherwise. */
    private boolean isH2;
    /** OrbisGIS DataManager. */
    private DataManager dataManager;
    private DataProcessingManager dataProcessingManager;
    /** OrbisGIS DriverFunctionContainer. */
    private DriverFunctionContainer driverFunctionContainer;
    /** Process manager which contains all the loaded scripts. */
    private ProcessManager processManager;
    private DataSourceService dataSourceService;
    /** Map containing object that can be used to cancel the loading of an URI. */
    private Map<URI, Object> cancelLoadMap;

    @Activate
    public void init(){
        processManager = new ProcessManager(dataSourceService, this);
        dataProcessingManager = new DataProcessingManager(this);
        setScriptFolder();
        loadPreviousState();

        //Find if the database used is H2 or not.
        //If yes, make all the processes wait for the previous one.
        multiThreaded = testDBForMultiProcess();
        if(!multiThreaded){
            if(isH2) {
                LoggerFactory.getLogger(LocalWpsServiceImplementation.class).warn("Warning, because of the H2 configuration," +
                        " the toolbox won't be able to run more than one process at the same time.\n" +
                        "Try to use the following setting for H2 : 'MVCC=TRUE; LOCK_TIMEOUT=100000;" +
                        " MULTI_THREADED=TRUE'");
            }
            else{
                LoggerFactory.getLogger(LocalWpsServiceImplementation.class).warn("Warning, because of the database configuration," +
                        " the toolbox won't be able to run more than one process at the same time.");
            }
        }
        cancelLoadMap = new HashMap<>();
    }

    @Deactivate
    public void dispose(){
        //Try to save the local files loaded.
        try {
            Properties tbProperties = new Properties();
            tbProperties.setProperty(PROPERTY_SOURCES, processManager.getListSourcesAsString());
            tbProperties.store(
                    new FileOutputStream(coreWorkspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES),
                    "Save of the OrbisGIS toolBox");
        } catch (IOException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).warn("Unable to save ToolBox state.");
        }
    }


    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    @Reference
    public void setDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = driverFunctionContainer;
    }
    public void unsetDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = null;
    }

    @Reference
    public void setDataSource(DataSource ds) {
        dataSourceService = (DataSourceService)ds;
    }
    public void unsetDataSource(DataSource ds) {
        dataSourceService = null;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
    }

    /**
     * Sets all the default OrbisGIS WPS script into the script folder of the .OrbisGIS folder.
     */
    private void setScriptFolder(){
        //Sets the WPS script folder
        File wpsScriptFolder = new File(coreWorkspace.getApplicationFolder(), WPS_SCRIPT_FOLDER);
        //Empty the script folder or create it
        if(wpsScriptFolder.exists()){
            if(wpsScriptFolder.listFiles() != null) {
                List<File> toDelete = Arrays.asList(wpsScriptFolder.listFiles());
                for(File f : toDelete){
                    f.delete();
                }
            }
        }
        else{
            if(!wpsScriptFolder.mkdir()){
                LoggerFactory.getLogger(LocalWpsServiceImplementation.class).warn("Unable to find or create a script folder.\n" +
                        "No basic script will be available.");
            }
        }
        if(wpsScriptFolder.exists() && wpsScriptFolder.isDirectory()){
            try {
                //Retrieve all the scripts url
                String folderPath = LocalWpsServiceImplementation.class.getResource("scripts").getFile();
                Enumeration<URL> enumUrl = FrameworkUtil.getBundle(LocalWpsServiceImplementation.class).findEntries(folderPath, "*", false);
                //For each url
                while(enumUrl.hasMoreElements()){
                    URL scriptUrl = enumUrl.nextElement();
                    String scriptPath = scriptUrl.getFile();
                    //Test if it's a groovy file
                    if(scriptPath.endsWith("."+GROOVY_EXTENSION)){
                        //If the script is already in the .OrbisGIS folder, remove it.
                        for(File existingFile : wpsScriptFolder.listFiles()){
                            if(existingFile.getName().endsWith(scriptPath) && existingFile.delete()){
                                LoggerFactory.getLogger(LocalWpsServiceImplementation.class).
                                        warn("Replacing script "+existingFile.getName()+" by the default one");
                            }
                        }
                        //Copy the script into the .OrbisGIS folder.
                        OutputStream out = new FileOutputStream(
                                new File(wpsScriptFolder.getAbsolutePath(),
                                        new File(scriptPath).getName()));
                        InputStream in = scriptUrl.openStream();
                        IOUtils.copy(in, out);
                        out.close();
                        in.close();
                    }
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(LocalWpsServiceImplementation.class).warn("Unable to copy the scripts. \n" +
                        "No basic script will be available. \n" +
                        "Error : "+e.getMessage());
            }
        }
        addLocalSource(wpsScriptFolder.toURI(), "orbisgis", true);
    }

    public void addLocalSource(URI uri, String iconName, boolean isDefaultScript){
        File file = new File(uri);
        if(file.isFile()){
            return;
        }
        for(File f : file.listFiles()) {
            addLocalScript(f, iconName, isDefaultScript);
        }
    }

    public List<ProcessIdentifier> getProcessIdentifierFromParent(URI parent){
        return processManager.getProcessIdentifierFromParent(parent);
    }

    public List<ProcessIdentifier> getCapabilities(){
        return processManager.getAllProcessIdentifier();
    }

    public ProcessIdentifier addLocalScript(File f, String iconName, boolean isDefaultScript){
        if(f.getName().endsWith(GROOVY_EXTENSION)) {
            processManager.addLocalScript(f.toURI(), iconName, isDefaultScript);
            ProcessIdentifier pi = processManager.getProcessIdentifier(f.toURI());
            if(pi != null) {
                return pi;
            }
        }
        return null;
    }

    public Process describeProcess(URI uri){
        return processManager.getProcess(uri);
    }

    public void removeProcess(URI uri){
        processManager.removeProcess(processManager.getProcess(uri));
    }

    public boolean checkProcess(URI uri){
        ProcessIdentifier pi = processManager.getProcessIdentifier(uri);
        if(pi != null){
            processManager.removeProcess(pi.getProcess());
        }
        return (processManager.addLocalScript(uri, pi.getCategory(), pi.isDefault()) != null);
    }

    public void execute(Process process, Map<URI, Object> dataMap, ProcessExecutionListener pel){

        pel.setStartTime(System.currentTimeMillis());
        Map<URI, Object> stash = new HashMap<>();
        //Catch all the Exception that can be thrown during the script execution.
        try {
            //Print in the log the process execution start
            pel.appendLog(ProcessExecutionListener.LogType.INFO, "Start the process");

            //Pre-process the data
            pel.appendLog(ProcessExecutionListener.LogType.INFO, "Pre-processing");
            for(DescriptionType inputOrOutput : process.getOutput()){
                stash.putAll(dataProcessingManager.preProcessData(inputOrOutput, dataMap, pel));
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                stash.putAll(dataProcessingManager.preProcessData(inputOrOutput, dataMap, pel));
            }

            //Execute the process and retrieve the groovy object.
            pel.appendLog(ProcessExecutionListener.LogType.INFO, "Execute the script");
            processManager.executeProcess(process, dataMap);

            //Post-process the data
            pel.appendLog(ProcessExecutionListener.LogType.INFO, "Post-processing");
            for(DescriptionType inputOrOutput : process.getOutput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, pel);
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, pel);
            }

            //Print in the log the process execution end
            pel.appendLog(ProcessExecutionListener.LogType.INFO, "End of the process");
            pel.setProcessState(ProcessExecutionListener.ProcessState.COMPLETED);
        }
        catch (Exception e) {
            pel.setProcessState(ProcessExecutionListener.ProcessState.ERROR);
            //Print in the log the process execution error
            pel.appendLog(ProcessExecutionListener.LogType.ERROR, e.getMessage());
            //Post-process the data
            pel.appendLog(ProcessExecutionListener.LogType.INFO, "Post-processing");
            for(DescriptionType inputOrOutput : process.getInput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, pel);
            }
        }
    }

    @Override
    public boolean checkFolder(URI uri){
        File f = new File(uri);
        if(f.exists() && f.isDirectory()){
            for(File file : f.listFiles()){
                if(file.getAbsolutePath().endsWith("."+GROOVY_EXTENSION)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Map<String, String> getImportableFormat(boolean onlySpatial){
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

    @Override
    public Map<String, String> getExportableFormat(boolean onlySpatial){
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

    @Override
    public List<String> getGeocatalogTableList(boolean onlySpatial) {
        List<String> list = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            //TODO find a better way to get only the user tabe and not the information table
            String defaultSchema = (isH2)?"PUBLIC":"public";
            List<String> tableList = JDBCUtilities.getTableNames(connection.getMetaData(), null,
                    null, null, SHOWN_TABLE_TYPES);
            //Test each table get from the JDBCUtilities
            for(String table : tableList){
                //If only the spatial table are allow filter them
                if(onlySpatial){
                    //Test if the table contains a geometrical field (if the table is spatial)
                    if(!SFSUtilities.getGeometryFields(connection, TableLocation.parse(table)).isEmpty()){
                        TableLocation tablelocation = TableLocation.parse(table, isH2);
                        //If the table is in the default schema, just add its name
                        if(tablelocation.getSchema().equals(defaultSchema)) {
                            list.add(tablelocation.getTable());
                        }
                        //If not, add the schema name '.' the table name (SCHEMA.TABLE)
                        else{
                            list.add(tablelocation.getSchema()+"."+tablelocation.getTable());
                        }
                    }
                }
                //Else add all the tables
                else{
                    TableLocation tablelocation = TableLocation.parse(table, isH2);
                    //If the table is in the default schema, just add its name
                    if(tablelocation.getSchema().equals(defaultSchema)) {
                        list.add(tablelocation.getTable());
                    }
                    //If not, add the schema name '.' the table name (SCHEMA.TABLE)
                    else{
                        list.add(tablelocation.getSchema()+"."+tablelocation.getTable());
                    }
                }
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Unable to get the geocatalog table " +
                    "list.\n"+e.getMessage());
        }
        return list;
    }

    @Override
    public Map<String, Object> getTableInformation(String tableName){
        Map<String, Object> map = new HashMap<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            tableName = TableLocation.parse(tableName, isH2).getTable();
            TableLocation tableLocation = new TableLocation(tableName);
            boolean isSpatial = !SFSUtilities.getGeometryFields(connection, tableLocation).isEmpty();
            int srid = SFSUtilities.getSRID(connection, tableLocation);
            //TODO : move this statement to SFSUtilities or JDBCUtilities to request the table dimension.
            Statement statement = connection.createStatement();
            String query = "SELECT COORD_DIMENSION FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME LIKE '"+
                    TableLocation.parse(tableName).getTable()+"';";
            ResultSet rs = statement.executeQuery(query);
            int dimension;
            if(rs.next()){
                dimension = rs.getInt(1);
            }
            else{
                dimension=0;
            }
            map.put(TABLE_IS_SPATIAL, isSpatial);
            map.put(TABLE_SRID, srid);
            map.put(TABLE_DIMENSION, dimension);
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Unable to get the table '"+tableName+
                    "' information.\n"+e.getMessage());
        }
        return map;
    }

    @Override
    public Map<String, Object> getFieldInformation(String tableName, String fieldName){
        Map<String, Object> map = new HashMap<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            TableLocation tableLocation = TableLocation.parse(tableName);
            List<String> geometricFields = SFSUtilities.getGeometryFields(connection, tableLocation);
            boolean isGeometric = false;
            for(String field : geometricFields){
                if(field.equals(fieldName)){
                    isGeometric = true;
                }
            }
            if(isGeometric) {
                int geometryId = SFSUtilities.getGeometryType(connection, tableLocation, fieldName);
                String geometryType = SFSUtilities.getGeometryTypeNameFromCode(geometryId);
                int srid = SFSUtilities.getSRID(connection, tableLocation);
                //TODO : move this statement to SFSUtilities or JDBCUtilities to request the table dimension.
                Statement statement = connection.createStatement();
                String query = "SELECT COORD_DIMENSION FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME LIKE '" +
                        TableLocation.parse(tableName).getTable() + "' AND F_GEOMETRY_COLUMN LIKE '" +
                        TableLocation.quoteIdentifier(fieldName) + "';";
                ResultSet rs = statement.executeQuery(query);
                int dimension;
                if (rs.next()) {
                    dimension = rs.getInt(1);
                } else {
                    dimension = 0;
                }
                map.put(GEOMETRY_TYPE, geometryType);
                map.put(TABLE_SRID, srid);
                map.put(TABLE_DIMENSION, dimension);
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Unable to the the field '"+
                    tableName+"."+fieldName+"' information.\n"+ e.getMessage());
        }
        return map;
    }

    @Override
    public List<String> getTableFieldList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes){
        List<String> fieldList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            DatabaseMetaData dmd = connection.getMetaData();
            TableLocation tablelocation = TableLocation.parse(tableName, isH2);
            ResultSet result = dmd.getColumns(tablelocation.getCatalog(), tablelocation.getSchema(),
                    tablelocation.getTable(), "%");
            while(result.next()){
                if (!dataTypes.isEmpty()) {
                    for (DataType dataType : dataTypes) {
                        if (DataType.testDBType(dataType, result.getObject(6).toString())) {
                            fieldList.add(result.getObject(4).toString());
                        }
                    }
                } else if(!excludedTypes.isEmpty()){
                    boolean accepted = true;
                    for (DataType dataType : excludedTypes) {
                        if (DataType.testDBType(dataType, result.getObject(6).toString())) {
                            accepted = false;
                        }
                    }
                    if(accepted) {
                        fieldList.add(result.getObject(4).toString());
                    }
                }else{
                    fieldList.add(result.getObject(4).toString());
                }
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Unable to get the table '"+tableName+
                    "' field list.\n"+e.getMessage());
        }
        return fieldList;
    }

    @Override
    public List<String> getFieldValueList(String tableName, String fieldName) {
        List<String> fieldValues = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            tableName = TableLocation.parse(tableName, isH2).getTable();
            List<String> fieldNames = JDBCUtilities.getFieldNames(connection.getMetaData(), tableName);
            for(String field : fieldNames){
                if(field.equalsIgnoreCase(fieldName)){
                    fieldName = field;
                    break;
                }
            }
            fieldValues.addAll(JDBCUtilities.getUniqueFieldValues(connection,
                    tableName,
                    fieldName));
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Unable to get the field '"+tableName+
                    "."+fieldName+"' value list.\n"+e.getMessage());
        }
        return fieldValues;
    }

    @Override
    public void removeTempTable(String tableName){
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            tableName = TableLocation.parse(tableName, isH2).getTable();
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Cannot remove the table '"+tableName+
                    "'.\n"+e.getMessage());
        }
    }

    @Override
    public void saveURI(URI uri, String tableName){
        try {
            tableName = TableLocation.parse(tableName, isH2).getTable();
            File f = new File(uri);
            if(!f.exists()){
                f.createNewFile();
            }
            //Find the good driver and save the file.
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                    extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
            driver.exportTable(dataManager.getDataSource().getConnection(), tableName,
                    f, new EmptyProgressVisitor());
        } catch (SQLException|IOException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Cannot save the table '"+tableName+
                    "'\n"+e.getMessage());
        }
    }

    @Override
    public String loadURI(URI uri, boolean copyInBase) {
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            File f = new File(uri);
            //Get the table name of the file
            String baseName = TableLocation.capsIdentifier(FilenameUtils.getBaseName(f.getName()), isH2);
            String tableName = dataManager.findUniqueTableName(baseName).replaceAll("\"", "");
            //Find the corresponding driver and load the file
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            Statement statement = connection.createStatement();
            if(extension.equalsIgnoreCase("csv")){
                //Save the statement to be able to cancel it.
                cancelLoadMap.put(uri, statement);
                statement.execute("CREATE TEMPORARY TABLE "+tableName+" AS SELECT * FROM CSVRead('"+f.getAbsolutePath()+"', NULL, 'fieldSeparator=;');");
                cancelLoadMap.remove(uri);
            }
            else {
                ProgressVisitor pv = new EmptyProgressVisitor();
                //Save the progress visitor to be able to cancel it.
                cancelLoadMap.put(uri, pv);
                if(copyInBase || !isH2){
                    DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                            extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
                    driver.importFile(dataManager.getDataSource().getConnection(), tableName, f, pv);
                }
                else {
                    DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                            extension, DriverFunction.IMPORT_DRIVER_TYPE.LINK);
                    driver.importFile(dataManager.getDataSource().getConnection(), tableName, f, pv);
                }
                cancelLoadMap.remove(uri);
            }
            return tableName;
        } catch (SQLException|IOException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error(e.getMessage());
        }
        return null;
    }

    @Override
    public void cancelLoadUri(URI uri){
        Object object = cancelLoadMap.get(uri);
        //If the object from the map is a Statement, cancel and close it.
        if(object instanceof Statement){
            try {
                ((Statement)object).cancel();
                ((Statement)object).close();
            } catch (SQLException e) {
                LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error("Unable to cancel the lodaing of '"+
                uri+"'.\n"+e.getMessage());
            }
        }
        //If the object from the map is a ProgressVisitor, cancel it.
        else if(object instanceof ProgressVisitor){
            ((ProgressVisitor)object).cancel();
        }
    }

    @Override
    public boolean isH2(){
        return isH2;
    }


    private void loadPreviousState(){
        Properties tbProperties = new Properties();
        File propertiesFile = new File(coreWorkspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES);
        if (propertiesFile.exists()) {
            try {
                tbProperties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                LoggerFactory.getLogger(LocalWpsServiceImplementation.class).warn("Unable to restore previous configuration of the ToolBox");
                tbProperties = null;
            }
        }
        if(tbProperties != null){
            Object prop = tbProperties.getProperty(PROPERTY_SOURCES);
            if(prop != null && !prop.toString().isEmpty()){
                String str = prop.toString();
                for(String s : str.split(";")){
                    addLocalScript(new File(URI.create(s)), null, false);
                }
            }
        }
    }

    private boolean testDBForMultiProcess(){
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            if(dataManager != null){
                isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                if(isH2) {
                    Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery("select VALUE from INFORMATION_SCHEMA.SETTINGS AS s where NAME = 'MVCC';");
                    result.next();
                    if (!result.getString(1).equals("TRUE")) {
                        return false;
                    }
                    result = statement.executeQuery("select VALUE from INFORMATION_SCHEMA.SETTINGS AS s where NAME = 'MULTI_THREADED';");
                    result.next();
                    if (!result.getString(1).equals("1")) {
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(LocalWpsServiceImplementation.class).error(e.getMessage());
        }
        return false;
    }

    @Override
    public void cancelProcess(URI uri){
        processManager.cancelProcess(processManager.getProcess(uri));
    }
}
