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

import org.h2gis.h2spatialapi.DriverFunction;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.orbistoolbox.controller.ProcessManager;
import org.orbisgis.orbistoolbox.controller.processexecution.dataprocessing.ProcessingManager;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ui.ProcessUIPanel;
import org.orbisgis.orbistoolbox.view.ui.ToolBoxPanel;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUIManager;
import org.orbisgis.orbistoolbox.view.utils.ProcessExecutionData;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.orbisgis.sif.SIFDialog;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 **/

@Component(service = DockingPanel.class)
public class ToolBox implements DockingPanel {

    /** Docking parameters used by DockingFrames */
    private DockingPanelParameters parameters;
    /** Process manager */
    private ProcessManager processManager;
    /** Displayed JPanel */
    private ToolBoxPanel toolBoxPanel;
    /** Object creating the UI corresponding to the data */
    private DataUIManager dataUIManager;
    /** DataManager */
    private static DataManager dataManager;
    private static DriverFunctionContainer driverFunctionContainer;
    private ProcessingManager processingManager;

    private Map<String, Object> properties;
    private List<ProcessExecutionData> processExecutionDataList;

    @Activate
    public void init(){
        toolBoxPanel = new ToolBoxPanel(this);
        processManager = new ProcessManager();
        dataUIManager = new DataUIManager();
        processExecutionDataList = new ArrayList<>();
        processingManager = new ProcessingManager(this);

        ActionCommands dockingActions = new ActionCommands();

        parameters = new DockingPanelParameters();
        parameters.setName("orbistoolbox");
        parameters.setTitle("OrbisToolBox");
        parameters.setTitleIcon(ToolBoxIcon.getIcon("orbistoolbox"));
        parameters.setCloseable(true);

        parameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(parameters));
    }

    @Deactivate
    public void dispose(){
        toolBoxPanel.dispose();
    }

    /**
     * Returns the process manager.
     * @return The process manager.
     */
    public ProcessManager getProcessManager(){
        return processManager;
    }

    public ProcessingManager getProcessingManager(){
        return processingManager;
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        return parameters;
    }

    @Override
    public JComponent getComponent() {
        return toolBoxPanel;
    }

    /**
     * Adds a local folder as a script source.
     */
    public void addNewLocalSource(){
        OpenFolderPanel openFolderPanel = new OpenFolderPanel("ToolBox.AddSource", "Add a source");
        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFolderPanel)){
            addLocalSource(openFolderPanel.getSelectedFile());
        }
    }

    public void addLocalSource(File file){
        processManager.addLocalSource(file.getAbsolutePath());
        toolBoxPanel.addLocalSource(file, processManager);
    }

    /**
     * Open the process window for the selected process
     */
    public void openProcess(){
        Process process = processManager.getProcess(toolBoxPanel.getSelectedNode().getFilePath());
        ProcessExecutionData processExecutionData = null;
        for(ProcessExecutionData puid : processExecutionDataList){
            if(puid.getProcess().getIdentifier().equals(process.getIdentifier())){
                processExecutionData = puid;
            }
        }
        ProcessUIPanel uiPanel;
        if(processExecutionData != null){
            uiPanel = new ProcessUIPanel(processExecutionData, this);
        }
        else{
            uiPanel = new ProcessUIPanel(process, this);
        }
        SIFDialog dialog = UIFactory.getSimpleDialog(uiPanel,
                SwingUtilities.getWindowAncestor(toolBoxPanel),
                true,
                "run",
                "close"
                );
        dialog.pack();
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        //Make the node listen to the process state.
        uiPanel.setProcessStateListener(toolBoxPanel.getNodesFromSelectedOne());
    }

    /**
     * Verify if the given file is a well formed script.
     * @param f File to check.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkProcess(File f){
        Process process = processManager.getProcess(f);
        if(process != null){
            processManager.removeProcess(process);
        }
        return (processManager.addLocalScript(f) != null);
    }

    /**
     * Remove the selected process in the tree.
     */
    public void removeProcess(File file){
        processManager.removeProcess(processManager.getProcess(file));
    }

    /**
     * Returns the DataUIManager.
     * @return The DataUIManager.
     */
    public DataUIManager getDataUIManager(){
        return dataUIManager;
    }

    /**
     * Save a processExecutionData to be able to retrieve it on reopening the process.
     * @param processExecutionData ProcessExecutionData to save.
     */
    public void saveProcessExecutionData(ProcessExecutionData processExecutionData){
        processExecutionDataList.add(processExecutionData);
    }

    /**
     * Deletes the processExecutionData.
     * @param processExecutionData ProcessExecutionData to delete.
     */
    public void deleteProcessExecutionData(ProcessExecutionData processExecutionData){
        processExecutionDataList.remove(processExecutionData);
    }

    /**
     * Returns true if the process from the given file is running, false otherwise.
     * @param file File of the process.
     * @return True if the process is running, false otherwise.
     */
    public boolean isProcessRunning(File file){
        Process process = processManager.getProcess(file);
        if(process != null) {
            for (ProcessExecutionData ped : processExecutionDataList){
                if (ped.getProcess().getIdentifier().equals(process.getIdentifier())){
                    if(ped.getState().equals(ProcessExecutionData.ProcessState.RUNNING)){
                        return true;
                    }
                }
            }
        }
        return false;
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
    public void setDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = driverFunctionContainer;
    }

    public void unsetDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = null;
    }

    public DriverFunctionContainer getDriverFunctionContainer(){
        return driverFunctionContainer;
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
            if(onlySpatial) {
                DatabaseMetaData md = connection.getMetaData();
                ResultSet rs = md.getTables(null, defaultSchema, "%", null);
                while (rs.next()) {
                    String tableName = rs.getString("F_TABLE_NAME");
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
}
