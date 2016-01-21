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

package org.orbisgis.orbistoolbox.view;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.h2spatialapi.EmptyProgressVisitor;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.orbistoolbox.controller.ProcessManager;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ui.ToolBoxPanel;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUIManager;
import org.orbisgis.orbistoolbox.view.utils.*;
import org.orbisgis.orbistoolbox.view.utils.dataProcessing.DataProcessingManager;
import org.orbisgis.orbistoolbox.view.utils.editor.log.LogEditableElement;
import org.orbisgis.orbistoolbox.view.utils.editor.log.LogEditor;
import org.orbisgis.orbistoolbox.view.utils.editor.process.ProcessEditableElement;
import org.orbisgis.orbistoolbox.view.utils.editor.process.ProcessEditor;
import org.orbisgis.orbistoolboxapi.annotations.model.FieldType;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.osgi.framework.FrameworkUtil;
import org.orbisgis.sif.edition.EditorDockable;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Start point of the OrbisToolBox.
 * This class instantiate all the tool and allow the different parts to communicates.
 *
 * @author Sylvain PALOMINOS
 **/

@Component
public class ToolBox implements DockingPanel  {
    /** String of the Groovy file extension. */
    public static final String GROOVY_EXTENSION = "groovy";
    /** String reference of the ToolBox used for DockingFrame. */
    public static final String TOOLBOX_REFERENCE = "orbistoolbox";
    private static final String WPS_SCRIPT_FOLDER = "scripts";
    private static final String PROPERTY_SOURCES = "PROPERTY_SOURCES";
    private static final String TOOLBOX_PROPERTIES = "toolbox.properties";
    private static boolean areScriptsCopied = false;

    /** Docking parameters used by DockingFrames. */
    private DockingPanelParameters parameters;
    /** Process manager which contains all the loaded scripts. */
    private ProcessManager processManager;
    /** Displayed JPanel. */
    private ToolBoxPanel toolBoxPanel;
    /** Object creating the UI corresponding to the data. */
    private DataUIManager dataUIManager;
    /** Map containing the properties to apply for the Grovvy script execution. */
    private Map<String, Object> properties;
    private DataProcessingManager dataProcessingManager;
    private CoreWorkspace coreWorkspace;

    /** EditableElement associated to the logEditor. */
    private LogEditableElement lee;
    /** EditorDockable for the displaying of the running processes log. */
    private LogEditor le;
    /** List of open EditorDockable. Used to close them when the ToolBox is close (Not stopped, just not visible). */
    private List<EditorDockable> openEditorList;

    /** OrbigGIS DockingManager. */
    private DockingManager dockingManager;
    /** OrbisGIS DataManager. */
    private static DataManager dataManager;
    /** OrbisGIS DriverFunctionContainer. */
    private static DriverFunctionContainer driverFunctionContainer;

    /** ToolBox properties */
    private Properties tbProperties;

    @Activate
    public void init(){
        toolBoxPanel = new ToolBoxPanel(this);
        processManager = new ProcessManager();
        dataUIManager = new DataUIManager(this);

        parameters = new DockingPanelParameters();
        parameters.setTitle("OrbisToolBox");
        parameters.setTitleIcon(ToolBoxIcon.getIcon("orbistoolbox"));
        parameters.setCloseable(true);
        parameters.setName(TOOLBOX_REFERENCE);

        ActionCommands dockingActions = new ActionCommands();
        parameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(parameters));

        dataProcessingManager = new DataProcessingManager(this);
        openEditorList = new ArrayList<>();
        lee = new LogEditableElement();
        le = null;

        if(!areScriptsCopied) {
            setScriptFolder();
        }
        // Try to load previous state of the toolBox.
        tbProperties = new Properties();
        File propertiesFile = new File(coreWorkspace.getApplicationFolder() + File.separator + TOOLBOX_PROPERTIES);
        if (propertiesFile.exists()) {
            try {
                tbProperties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                LoggerFactory.getLogger(ToolBox.class).warn("Unable to restore previous configuration of the ToolBox");
                tbProperties = null;
            }
        }
        if(tbProperties != null){
            Object prop = tbProperties.getProperty(PROPERTY_SOURCES);
            if(prop != null){
                String str = prop.toString();
                for(String s : str.split(";")){
                    addLocalSource(URI.create(s));
                }
            }
        }
    }

    /**
     * Sets all the default OrbisGIS WPS script into the script folder of the .OrbisGIS folder.
     */
    private void setScriptFolder(){
        //Sets the WPS script folder
        File wpsScriptFolder = new File(coreWorkspace.getApplicationFolder(), WPS_SCRIPT_FOLDER);
        if(!wpsScriptFolder.exists()){
            if(!wpsScriptFolder.mkdir()){
                LoggerFactory.getLogger(ToolBox.class).warn("Unable to find or create a script folder.\n" +
                        "No basic script will be available.");
            }
        }
        if(wpsScriptFolder.exists() && wpsScriptFolder.isDirectory()){
            try {
                //Retrieve all the scripts url
                String folderPath = ToolBox.class.getResource("scripts").getFile();
                Enumeration<URL> enumUrl = FrameworkUtil.getBundle(ToolBox.class).findEntries(folderPath, "*", false);
                //For each url
                while(enumUrl.hasMoreElements()){
                    URL scriptUrl = enumUrl.nextElement();
                    String scriptPath = scriptUrl.getFile();
                    //Test if it's a groovy file
                    if(scriptPath.endsWith("."+GROOVY_EXTENSION)){
                        //If the script is already in the .OrbisGIS folder, remove it.
                        for(File existingFile : wpsScriptFolder.listFiles()){
                            if(existingFile.getName().endsWith(scriptPath) && existingFile.delete()){
                                LoggerFactory.getLogger(ToolBox.class).
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
                LoggerFactory.getLogger(ToolBox.class).warn("Unable to copy the scripts. \n" +
                        "No basic script will be available. \n" +
                        "Error : "+e.getMessage());
            }
        }
        areScriptsCopied = true;
        addLocalSource(wpsScriptFolder.toURI());
    }

    @Deactivate
    public void dispose(){
        //Removes all the EditorDockable that were added
        for(EditorDockable ed : openEditorList){
            dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
        }
        openEditorList = new ArrayList<>();
        toolBoxPanel.dispose();
        areScriptsCopied = false;

        //Try to save the local files loaded.
        try {
            if (tbProperties == null) {
                tbProperties = new Properties();
            }
            tbProperties.setProperty(PROPERTY_SOURCES, toolBoxPanel.getListLocalSourcesAsString());
            tbProperties.store(
                    new FileOutputStream(coreWorkspace.getApplicationFolder() + File.separator + TOOLBOX_PROPERTIES),
                    "Save of the OrbisGIS toolBox");
        } catch (IOException e) {
            LoggerFactory.getLogger(ToolBox.class).warn("Unable to save ToolBox state.");
        }
    }

    /**
     * Returns the process manager.
     * @return The process manager.
     */
    public ProcessManager getProcessManager(){
        return processManager;
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        //when the toolBox is not visible, it mean that is was closed, so close all the toolbox editors
        if(!parameters.isVisible()){
            for(EditorDockable ed : openEditorList) {
                if (ed instanceof ProcessEditor) {
                    ((ProcessEditor)ed).setAlive(false);
                }
                dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
            }
            openEditorList = new ArrayList<>();
            lee.removePropertyChangeListener(le);
            le = null;
        }
        return parameters;
    }

    /**
     * Close the given EditorDockable if it was add by the ToolBox (contained by openEditorList).
     * @param ed EditorDockable to close.
     */
    public void killEditor(EditorDockable ed) {
        if(openEditorList.contains(ed)){
            dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
        }
        openEditorList.remove(ed);
    }

    @Override
    public JComponent getComponent() {
        return toolBoxPanel;
    }

    /**
     * Open a file browser to find a local script folder and add it.
     * Used in an EvenHandler in view.ui.ToolBoxPanel
     */
    public void addNewLocalSource(){
        OpenFolderPanel openFolderPanel = new OpenFolderPanel("ToolBox.AddSource", "Add a source");
        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFolderPanel)){
            addLocalSource(openFolderPanel.getSelectedFile().toURI());
        }
    }

    /**
     * Adds a folder as a local script source.
     * @param uri Folder URI where the script are located.
     */
    public void addLocalSource(URI uri){
        processManager.addLocalSource(uri);
        toolBoxPanel.addLocalSource(uri, processManager);
    }

    /**
     * Open the process window for the selected process.
     * @param scriptUri Script URI to execute as a process.
     * @return The ProcessEditableElement which contains the running process information (log, state, ...).
     */
    public ProcessEditableElement openProcess(URI scriptUri){
        Process process = processManager.getProcess(scriptUri);
        ProcessEditableElement pee = new ProcessEditableElement(process);
        ProcessEditor pe = new ProcessEditor(this, pee);
        //Find if there is already a ProcessEditor open with the same process.
        //If not, add the new one.
        boolean alreadyOpen = false;
        for(EditorDockable ed : openEditorList){
            if(ed.getDockingParameters().getName().equals(pe.getDockingParameters().getName())){
                alreadyOpen = true;
            }
        }
        if(!alreadyOpen) {
            dockingManager.addDockingPanel(pe);
            openEditorList.add(pe);
        }
        else{
            LoggerFactory.getLogger(ToolBox.class).warn("The process '"+pee.getProcess().getTitle()+"' is already open.");
        }
        return pee;
    }

    /**
     * Once the process is configured and run, add it to the LogEditor and removes the ProcessEditor (close it).
     * @param pe ProcessEditor to close.
     */
    public void validateInstance(ProcessEditor pe){
        ProcessEditableElement pee = (ProcessEditableElement) pe.getEditableElement();
        //If the LogEditor is not displayed, just do it <°>.
        if(le == null) {
            le = new LogEditor(lee);
            dockingManager.addDockingPanel(le);
            openEditorList.add(le);
        }

        lee.addProcessEditableElement(pee);
        dockingManager.removeDockingPanel(pe.getDockingParameters().getName());
        openEditorList.remove(pe);
    }

    /**
     * Verify if the given file is a well formed script.
     * @param uri URI to check.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkProcess(URI uri){
        Process process = processManager.getProcess(uri);
        if(process != null){
            processManager.removeProcess(process);
        }
        return (processManager.addLocalScript(uri) != null);
    }

    /**
     * Verify if the given file is a well formed script.
     * @param uri URI to check.
     * @return True if the file is well formed, false otherwise.
     */
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

    /**
     * Remove the selected process in the tree.
     */
    public void removeProcess(URI uri){
        processManager.removeProcess(processManager.getProcess(uri));
    }

    /**
     * Returns the DataUIManager.
     * @return The DataUIManager.
     */
    public DataUIManager getDataUIManager(){
        return dataUIManager;
    }

    public Map<String, Object> getProperties(){
        return properties;
    }

    public ToolBox(){
        properties = new HashMap<>();
    }

    @Reference
    public void setDataSource(javax.sql.DataSource ds) {
        properties.put("ds", ds);
    }

    public void unsetDataSource(javax.sql.DataSource ds) {
        properties.remove("ds");
    }

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }

    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        ToolBox.dataManager = dataManager;
    }

    public void unsetDataManager(DataManager dataManager) {
        ToolBox.dataManager = null;
    }

    public DataManager getDataManager(){
        return dataManager;
    }

    @Reference
    public void setDockingManager(DockingManager dockingManager) {
        this.dockingManager = dockingManager;
    }

    public void unsetDockingManager(DockingManager dockingManager) {
        this.dockingManager = null;
    }

    @Reference
    public void setDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        ToolBox.driverFunctionContainer = driverFunctionContainer;
    }

    public void unsetDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        ToolBox.driverFunctionContainer = null;
    }

    public DriverFunctionContainer getDriverFunctionContainer(){
        return driverFunctionContainer;
    }

    public DataProcessingManager getDataProcessingManager() {
        return dataProcessingManager;
    }

    public ToolBoxPanel getToolBoxPanel(){
        return toolBoxPanel;
    }

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
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return list;
    }

    /**
     * Return the list of the field of a table.
     * @param tableName Name of the table.
     * @param fieldTypes Type of the field accepted. If empty, accepts all the field.
     * @return The list of the field name.
     */
    public static List<String> getTableFieldList(String tableName, List<FieldType> fieldTypes){
        List<String> fieldList = new ArrayList<>();
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet result = dmd.getColumns(connection.getCatalog(), null, tableName, "%");
            //TODO : replace the value 3, 4 ... with constants taking into account the database used (H2, postgres ...).
            while(result.next()){
                if (!fieldTypes.isEmpty()) {
                    for (FieldType fieldType : fieldTypes) {
                        if (fieldType.name().equalsIgnoreCase(result.getObject(6).toString())) {
                            fieldList.add(result.getObject(4).toString());
                        }
                    }
                } else{
                    fieldList.add(result.getObject(4).toString());
                }
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return fieldList;
    }

    /**
     * Loads the given file into the geocatalog and return its table name.
     * @param uri URI to load.
     * @return Table name of the loaded file. Returns null if the file can't be loaded.
     */
    public String loadURI(URI uri) {
        try {
            File f = new File(uri);
            if(f.isDirectory()){
                return null;
            }
            //Get the table name of the file
            String baseName = TableLocation.capsIdentifier(FilenameUtils.getBaseName(f.getName()), true);
            String tableName = dataManager.findUniqueTableName(baseName).replaceAll("\"", "");
            //Find the corresponding driver and load the file
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                    extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
            driver.importFile(dataManager.getDataSource().getConnection(), tableName, f, new EmptyProgressVisitor());
            return tableName;
        } catch (SQLException|IOException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return null;
    }

    /**
     * Save a geocatalog table into a file.
     * @param uri URI where the table will be saved.
     * @param tableName Name of the table to save.
     */
    public void saveURI(URI uri, String tableName){
        try {
            File f = new File(uri);
            //Find the good driver and save the file.
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                    extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
            driver.exportTable(dataManager.getDataSource().getConnection(), tableName, f, new EmptyProgressVisitor());
        } catch (SQLException|IOException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
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
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return fieldValues;
    }
}
