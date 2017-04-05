/**
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
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.wpsserviceorbisgis;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.DescribeProcess;
import net.opengis.wps._2_0.ProcessOfferings;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.*;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wpsservice.WpsServer;
import org.orbisgis.wpsservice.WpsServerImpl;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.controller.process.ProcessManager;
import org.orbisgis.wpsservice.controller.utils.Job;
import org.orbisgis.wpsservice.model.DataType;
import org.orbisgis.wpsserviceorbisgis.utils.OrbisGISWpsServerListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is an implementation of the LocalWpsService interface and is declared a OSGI component.
 * It gives all the methods needed by the a WPS client to be able to get a process, to configure it and to run it.
 * It also implements the DatabaseProgressionListener to be able to know the table list in the database.
 */
@Component(service = {OrbisGISWpsServer.class, WpsServer.class})
public class OrbisGISWpsServerImpl
        extends WpsServerImpl
        implements OrbisGISWpsServer, DatabaseProgressionListener {

    private static final String TOOLBOX_PROPERTIES = "toolbox.properties";
    private static final String PROPERTY_SOURCES = "PROPERTY_SOURCES";
    /**Array of the table type accepted. */
    private static final String[] SHOWN_TABLE_TYPES = new String[]{"TABLE","LINKED TABLE","VIEW","EXTERNAL"};
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrbisGISWpsServerImpl.class);
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(OrbisGISWpsServerImpl.class);

    /** True if the database is H2, false otherwise. */
    private boolean isH2;
    /** True if an updates happen while another on is running. */
    private boolean updateWhileAwaitingRefresh = false;
    /** True if a swing runnable is pending to refresh the content of the table list, false otherwise. */
    private AtomicBoolean awaitingRefresh=new AtomicBoolean(false);
    /** CoreWorkspace of OrbisGIS */
    private CoreWorkspace coreWorkspace;
    /** OrbisGIS DataManager. */
    private DataManager dataManager;
    /** List of map containing the table with their basic information.
     * It is used as a buffer to avoid to reload all the table list to save time.
     */
    private List<Map<String, String>> tableList;
    /** List of OrbisGISWpsServerListener. */
    private List<OrbisGISWpsServerListener> orbisgisWpsServerListenerList = new ArrayList<>();


    /**********************************************/
    /** Initialisation method of the WPS service **/
    /**********************************************/

    /**
     * Initialization of the LocalWpsServiceImplementation required by OSGI.
     */
    @Activate
    public void initialisation(){
        //Start the listening of the database
        initDataBaseLink();
        //Restore the last saved state of the wps server
        loadPreviousState();
    }

    /**
     * Reload the script loaded in the previous session.
     */
    private void loadPreviousState(){
        if(coreWorkspace != null) {
            Properties tbProperties = new Properties();
            //Load the property file
            File propertiesFile = new File(coreWorkspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES);
            if (propertiesFile.exists()) {
                try {
                    tbProperties.load(new FileInputStream(propertiesFile));
                } catch (IOException e) {
                    LOGGER.warn(I18N.tr("Unable to restore previous configuration of the ToolBox."));
                    tbProperties = new Properties();
                }
            }

            //Properties loading
            Object prop = tbProperties.getProperty(PROPERTY_SOURCES);
            if(prop != null && !prop.toString().isEmpty()){
                String str = prop.toString();
                for(String s : str.split(";")){
                    File f = new File(URI.create(s));
                    addProcess(f, null, true, new File(f.getParent()).getName());
                }
            }
        }
        else{
            LOGGER.warn("Warning, no CoreWorkspace found. Unable to load the previous state.");
        }
    }

    /**
     * Initialize all the mechanism using the database
     */
    private void initDataBaseLink(){
        if(dataManager != null) {
            //Find if the database used is H2 or not.
            //If yes, make all the processes wait for the previous one.
            multiThreaded = testDBForMultiProcess();
            //TODO Remove this user message and implements the parallel process execution
            /*
            if (!multiThreaded) {
                if (isH2) {
                    LOGGER.warn(I18N.tr("Warning, because of the H2 configuration," +
                            " the toolbox won't be able to run more than one process at the same time.\n" +
                            "Try to use the following setting for H2 : 'MVCC=TRUE; LOCK_TIMEOUT=100000;" +
                            " MULTI_THREADED=TRUE'"));
                } else {
                    LOGGER.warn(I18N.tr("Warning, because of the database configuration," +
                            " the toolbox won't be able to run more than one process at the same time."));
                }
            }*/
            //Install database listeners
            dataManager.addDatabaseProgressionListener(this, StateEvent.DB_STATES.STATE_STATEMENT_END);
            //Call readDatabase when a SourceManager fire an event
            onDataManagerChange();
        }
        else{
            LOGGER.warn(I18N.tr("Warning, no DataManager found."));
        }
    }


    /**********************************************/
    /** Deactivation methods of the WPS service. **/
    /**********************************************/

    /**
     * Dispose of the LocalWpsServiceImplementation required by OSGI.
     */
    @Deactivate
    public void dispose(){
        //Cancel all running job
        for(Map.Entry<UUID, Job> entry : this.getJobMap().entrySet()){
            cancelProcess(entry.getKey());
        }
        //Try to save the local files loaded.
        try {
            Properties tbProperties = new Properties();
            //Save the open process source path
            tbProperties.setProperty(PROPERTY_SOURCES, this.getProcessManager().getListSourcesAsString());
            tbProperties.store(
                    new FileOutputStream(coreWorkspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES),
                    I18N.tr("Save of the OrbisGIS toolBox"));
        } catch (IOException e) {
            LOGGER.warn(I18N.tr("Unable to save ToolBox state."));
        }
    }


    /******************************************************************/
    /** Set and Unset methods to get services from OrbisGIS via OSGI **/
    /******************************************************************/

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    @Reference
    @Override
    public void setDataSource(DataSource ds) {
        super.setDataSource(ds);
    }
    public void unsetDataSource(DataSource ds) {
        super.setDataSource(null);
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
    }

    @Reference
    @Override
    public void setExecutorService(ExecutorService executorService) {
        super.setExecutorService(executorService);
    }
    public void unsetExecutorService(ExecutorService executorService) {
        super.setExecutorService(null);
    }


    /***********************/
    /** Utilities methods **/
    /***********************/

    @Override
    public boolean checkProcess(URI identifier){
        ProcessManager processManager = this.getProcessManager();
        CodeType codeType = new CodeType();
        codeType.setValue(identifier.toString());
        ProcessIdentifier pi = processManager.getProcessIdentifier(codeType);
        //If the URI correspond to a ProcessIdentifier remove it before adding it again
        if(pi != null){
            //If the file corresponding to the URI does not exist anymore, remove if and warn the user.
            File f = new File(pi.getSourceFileURI());
            if(!f.exists()){
                processManager.removeProcess(pi.getProcessDescriptionType());
                LOGGER.error(I18N.tr("The script {0} does not exist anymore.", f.getAbsolutePath()));
                return false;
            }
            processManager.removeProcess(pi.getProcessDescriptionType());
            processManager.addScript(pi.getSourceFileURI(), pi.getCategory(), pi.isRemovable(), pi.getNodePath());

            return (processManager.getProcess(pi.getProcessDescriptionType().getIdentifier()) != null);
        }
        return false;
    }

    @Override
    public ProcessOfferings describeProcess(DescribeProcess describeProcess){
        this.onDataManagerChange();
        return super.describeProcess(describeProcess);
    }

    @Override
    public List<String> getTableList(List<DataType> dataTypes, List<DataType> excludedTypes) {
        List<String> list = new ArrayList<>();
        String defaultSchema = (isH2)?"PUBLIC":"public";
        //Read the tableList to get the desired tables
        for(Map<String, String> map : tableList){
            if(map.containsKey(TABLE_LOCATION)) {
                TableLocation tablelocation = TableLocation.parse(map.get(TABLE_LOCATION), isH2);
                boolean isValid = false;
                if((dataTypes == null || dataTypes.isEmpty()) && (excludedTypes == null || excludedTypes.isEmpty())){
                    isValid = true;
                }
                else if(map.containsKey(COLUMN_TYPE)) {
                    try (Connection connection = dataManager.getDataSource().getConnection()) {
                        Map<String, Integer> types = SFSUtilities.getGeometryTypes(connection, tablelocation);
                        for (Map.Entry<String, Integer> entry : types.entrySet()) {
                            if(dataTypes != null) {
                                for (DataType dataType : dataTypes) {
                                    if (DataType.testGeometryType(dataType, entry.getValue())) {
                                        isValid = true;
                                    }
                                }
                            }
                            if(excludedTypes != null) {
                                for (DataType dataType : excludedTypes) {
                                    if (DataType.testGeometryType(dataType, entry.getValue())) {
                                        isValid = false;
                                    }
                                }
                            }
                        }
                    } catch (SQLException e) {
                        LOGGER.error(I18N.tr("Unable to get the connection.\nCause : {0}.",
                                e.getMessage()));
                    }
                }
                else {
                    try (Connection connection = dataManager.getDataSource().getConnection()) {
                        //Get the metadata of the table
                        ResultSet rs = connection.createStatement().executeQuery(String.format("select * from %s limit 1", tablelocation.getTable()));
                        ResultSetMetaData metaData = rs.getMetaData();
                        //For each column, get its DataType
                        for(int columnId = 1; columnId <= metaData.getColumnCount(); ++columnId) {
                            String columnTypeName = metaData.getColumnTypeName(columnId);
                            if(!columnTypeName.equalsIgnoreCase("geometry")) {
                                DataType dataType = DataType.getDataType(metaData.getColumnType(columnId));
                                //Tests if the DataType is compatible with the acceptedTypes and excludedTypes.
                                if(dataTypes != null && !dataTypes.isEmpty()) {
                                    for (DataType acceptedType : dataTypes) {
                                        if (dataType.equals(acceptedType)) {
                                            isValid = true;
                                        }
                                    }
                                }
                                else{
                                    isValid = true;
                                }
                                if(excludedTypes != null && !excludedTypes.isEmpty()) {
                                    for (DataType excludedType : excludedTypes) {
                                        if (excludedType.equals(dataType)) {
                                            isValid = false;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (SQLException e) {
                        LOGGER.error(I18N.tr("Unable to get the connection.\nCause : {0}.",
                                e.getMessage()));
                    }
                }

                if (isValid) {
                    //If the table is in the default schema, just add its name
                    if (tablelocation.getSchema(defaultSchema).equals(defaultSchema)) {
                        list.add(tablelocation.getTable());
                    }
                    //If not, add the schema name '.' the table name (SCHEMA.TABLE)
                    else {
                        list.add(tablelocation.getSchema() + "." + tablelocation.getTable());
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getColumnInformation(String tableName){
        List<Map<String, Object>> mapList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            //Get the list of the columns of a table
            ResultSet rs1 = connection.createStatement().executeQuery(String.format("select * from %s limit 1", tableName));
            ResultSetMetaData metaData = rs1.getMetaData();
            //If the column isn't a geometry, add it to the map
            for(int i=1; i<=metaData.getColumnCount(); i++){
                if(!metaData.getColumnTypeName(i).equalsIgnoreCase("GEOMETRY")){
                    Map<String, Object> map = new HashMap<>();
                    map.put(COLUMN_NAME, metaData.getColumnLabel(i));
                    map.put(COLUMN_TYPE, metaData.getColumnTypeName(i));
                    map.put(COLUMN_SRID, 0);
                    map.put(COLUMN_DIMENSION, 0);
                    mapList.add(map);
                }
            }
            //Once the non geometric columns are get, do the same with the geometric one.
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME LIKE '" +
                    TableLocation.parse(tableName).getTable() + "';";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                //Case of H2 database
                if(isH2) {
                    map.put(COLUMN_NAME, rs.getString(4));
                    map.put(COLUMN_TYPE, SFSUtilities.getGeometryTypeNameFromCode(rs.getInt(6)));
                    map.put(COLUMN_SRID, rs.getInt(8));
                    map.put(COLUMN_DIMENSION, rs.getInt(7));
                }
                //Other case
                else{
                    map.put(COLUMN_NAME, rs.getString(4));
                    map.put(COLUMN_TYPE, rs.getString(7));
                    map.put(COLUMN_SRID, rs.getInt(6));
                    map.put(COLUMN_DIMENSION, rs.getInt(5));
                }
                mapList.add(map);
            }
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Unable to get the column INFORMATION of the table {0} information.\nCause : {1}.",
                    tableName, e.getMessage()));
        }
        return mapList;
    }

    @Override
    public List<String> getColumnList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes){
        if(dataTypes == null){
            dataTypes = new ArrayList<>();
        }
        if(excludedTypes == null){
            excludedTypes = new ArrayList<>();
        }
        List<String> columnList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            DatabaseMetaData dmd = connection.getMetaData();
            TableLocation tablelocation = TableLocation.parse(tableName, isH2);
            ResultSet result = dmd.getColumns(tablelocation.getCatalog(), tablelocation.getSchema(),
                    tablelocation.getTable(), "%");
            while(result.next()){
                if (!dataTypes.isEmpty()) {
                    for (DataType dataType : dataTypes) {
                        String type = result.getObject(6).toString();
                        if(type.equalsIgnoreCase("GEOMETRY")){
                            if (DataType.testGeometryType(dataType, SFSUtilities.getGeometryType(connection,
                                    tablelocation, result.getObject(4).toString().toUpperCase()))) {
                                columnList.add(result.getObject(4).toString());
                            }
                        }
                        else {
                            if (DataType.testDBType(dataType, result.getObject(6).toString().toUpperCase())) {
                                columnList.add(result.getObject(4).toString());
                            }
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
                        columnList.add(result.getObject(4).toString());
                    }
                }else{
                    columnList.add(result.getObject(4).toString());
                }
            }
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Unable to get the table {0} column list.\nCause : {1}.", tableName, e.getMessage()));
        }
        return columnList;
    }

    @Override
    public List<String> getValueList(String tableName, String columnName) {
        List<String> values = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            tableName = TableLocation.parse(tableName, isH2).toString();
            values.addAll(JDBCUtilities.getUniqueFieldValues(connection,
                    tableName,
                    columnName));
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Unable to get the column {0}.{1} value list.\nCause : {2}.",
                    tableName, columnName, e.getMessage()));
        }
        return values;
    }

    @Override
    public List<String> getSRIDList(){
        List<String> sridList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT srid, AUTH_NAME FROM SPATIAL_REF_SYS");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sridList.add(resultSet.getString("AUTH_NAME") + ":" +resultSet.getInt("srid"));
            }
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Error on getting the SRID list.\nCause : {0}.", e.getMessage()));
        }
        return sridList;
    }

    @Override
    public void addOrbisGISWpsServerListener(OrbisGISWpsServerListener wpsServerListener) {
        this.orbisgisWpsServerListenerList.add(wpsServerListener);
    }

    @Override
    public void removeOrbisGISWpsServerListener(OrbisGISWpsServerListener wpsServerListener) {
        this.orbisgisWpsServerListenerList.remove(wpsServerListener);
    }

    /**
     * Test the database an returns if it allows the wps service to run more than one process at the same time.
     * @return True if more than one process can be run at the same time, false otherwise.
     */
    private boolean testDBForMultiProcess(){
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            if(dataManager != null){
                isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                if(isH2){
                    this.setDatabase(Database.H2GIS);
                }
                else{
                    this.setDatabase(Database.POSTGIS);
                }
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
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public List<ProcessIdentifier> addProcess(File f, String[] iconName, boolean isRemovable, String nodePath){
        List<ProcessIdentifier> piList = super.addProcess(f, iconName, isRemovable, nodePath);
        for(OrbisGISWpsServerListener listener : orbisgisWpsServerListenerList){
            listener.onScriptAdd();
        }
        return piList;
    }

    @Override
    public void removeProcess(URI identifier){
        super.removeProcess(identifier);
        for(OrbisGISWpsServerListener listener : orbisgisWpsServerListenerList){
            listener.onScriptRemoved();
        }
    }


    /*******************************************************/
    /** Methods for the listening of the database update. **/
    /*******************************************************/

    /**
     * Method called when a change happens in the DataManager (i.e. a table suppression, a table add ...)
     */
    public void onDataManagerChange() {
        //If not actually doing a refresh, do it.
        if(!awaitingRefresh.getAndSet(true)) {
            ReadDataManagerOnSwingThread worker = new ReadDataManagerOnSwingThread(this);
            ExecutorService executorService = getExecutorService();
            if(executorService != null){
                executorService.execute(worker);
            }
            else{
                worker.run();
            }
        } else {
            updateWhileAwaitingRefresh = true;
        }
    }

    @Override
    public void progressionUpdate(StateEvent state) {
        if (state.isUpdateDatabaseStructure()) {
            onDataManagerChange();
        }
    }

    /**
     * Read the table list in the database
     */
    protected void readDatabase() {
        List<Map<String, String>> newTables = new ArrayList<>();
        try (Connection connection = dataManager.getDataSource().getConnection()) {
            final String defaultCatalog = connection.getCatalog();
            String defaultSchema = "PUBLIC";
            try {
                if (connection.getSchema() != null) {
                    defaultSchema = connection.getSchema();
                }
            } catch (AbstractMethodError | Exception ex) {
                // Driver has been compiled with JAVA 6, or is not implemented
            }
            // Fetch Geometry tables
            Map<String,String> tableGeometry = new HashMap<>();
            try(Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM "+defaultSchema+".geometry_columns")) {
                while(rs.next()) {
                    tableGeometry.put(new TableLocation(rs.getString("F_TABLE_CATALOG"),
                            rs.getString("F_TABLE_SCHEMA"), rs.getString("F_TABLE_NAME")).toString(), rs.getString("TYPE"));
                }
            } catch (SQLException ex) {
                LOGGER.warn(I18N.tr("Geometry columns information of tables are not available.", ex));
            }
            // Fetch all tables
            try(ResultSet rs = connection.getMetaData().getTables(null, null, null, SHOWN_TABLE_TYPES)) {
                while(rs.next()) {
                    Map<String, String> tableAttr = new HashMap<>();
                    TableLocation location = new TableLocation(rs);
                    if(location.getCatalog().isEmpty()) {
                        // PostGIS return empty catalog on metadata
                        location = new TableLocation(defaultCatalog, location.getSchema(), location.getTable());
                    }
                    // Make Label
                    StringBuilder label = new StringBuilder(addQuotesIfNecessary(location.getTable()));
                    if(!location.getSchema().isEmpty() && !location.getSchema().equalsIgnoreCase(defaultSchema)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getSchema()));
                    }
                    if(!location.getCatalog().isEmpty() && !location.getCatalog().equalsIgnoreCase(defaultCatalog)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getCatalog()));
                    }
                    // Shortcut location for H2 database
                    TableLocation shortLocation;
                    if(isH2) {
                        shortLocation = new TableLocation("",
                                location.getSchema().equals(defaultSchema) ? "" : location.getSchema(),
                                location.getTable());
                    } else {
                        shortLocation = new TableLocation(location.getCatalog().equalsIgnoreCase(defaultCatalog) ?
                                "" : location.getCatalog(),
                                location.getCatalog().equalsIgnoreCase(defaultCatalog) &&
                                        location.getSchema().equalsIgnoreCase(defaultSchema) ? "" : location.getSchema(),
                                location.getTable());
                    }
                    tableAttr.put(TABLE_LOCATION, shortLocation.toString(isH2));
                    tableAttr.put(TABLE_LABEL, label.toString());
                    String type = tableGeometry.get(location.toString());
                    if(type != null) {
                        tableAttr.put(COLUMN_TYPE, type);
                    }
                    newTables.add(tableAttr);
                }
            }
            tableList = newTables;
        } catch (SQLException ex) {
            LOGGER.error(I18N.tr("Cannot read the table list", ex));
        }
    }

    /**
     * If needed, quote the table location part
     * @param tableLocationPart Table location part to quote.
     * @return Quoted table location part.
     */
    private static String addQuotesIfNecessary(String tableLocationPart) {
        if(tableLocationPart.contains(".")) {
            return "\""+tableLocationPart+"\"";
        } else {
            return tableLocationPart;
        }
    }

    /**
     * Refresh the list
     */
    private static class ReadDataManagerOnSwingThread implements Runnable {
        private OrbisGISWpsServerImpl wpsService;

        private ReadDataManagerOnSwingThread(OrbisGISWpsServerImpl wpsService) {
            this.wpsService = wpsService;
        }

        @Override
        public void run() {
            wpsService.readDatabase();
            //Refresh the list on the swing thread
            wpsService.awaitingRefresh.set(false);
            // An update occurs during fetching tables
            if(wpsService.updateWhileAwaitingRefresh) {
                wpsService.updateWhileAwaitingRefresh = false;
                wpsService.onDataManagerChange();
            }
        }
    }
}
