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

package org.orbisgis.wpsclient;

import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.wpsclient.view.ui.ToolBoxPanel;
import org.orbisgis.wpsclient.view.ui.dataui.DataUIManager;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.editor.log.LogEditableElement;
import org.orbisgis.wpsclient.view.utils.editor.log.LogEditor;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditor;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.model.DescriptionType;
import org.orbisgis.wpsservice.model.Process;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Start point of the OrbisToolBox.
 * This class instantiate all the tool and allow the different parts to communicates.
 *
 * @author Sylvain PALOMINOS
 **/

@Component(immediate = true, service = {DockingPanel.class, WpsClient.class})
public class WpsClientImpl implements DockingPanel, WpsClient {
    /** String reference of the ToolBox used for DockingFrame. */
    public static final String TOOLBOX_REFERENCE = "orbistoolbox";

    /** Docking parameters used by DockingFrames. */
    private DockingPanelParameters parameters;
    /** Displayed JPanel. */
    private ToolBoxPanel toolBoxPanel;
    /** Object creating the UI corresponding to the data. */
    private DataUIManager dataUIManager;

    /** EditableElement associated to the logEditor. */
    private LogEditableElement lee;
    /** EditorDockable for the displaying of the running processes log. */
    private LogEditor le;
    /** List of open EditorDockable. Used to close them when the ToolBox is close (Not stopped, just not visible). */
    private List<EditorDockable> openEditorList;

    /** OrbigGIS DockingManager. */
    private DockingManager dockingManager;
    /** OrbigGIS ExecutorService. */
    private ExecutorService executorService;
    /** OrbisGIS DataManager. */
    private static DataManager dataManager;
    private LocalWpsService wpsService;
    private ProcessEditor pe;

    @Activate
    public void init(){
        toolBoxPanel = new ToolBoxPanel(this);
        dataUIManager = new DataUIManager(this);

        parameters = new DockingPanelParameters();
        parameters.setTitle("ToolBox");
        parameters.setTitleIcon(ToolBoxIcon.getIcon("orbistoolbox"));
        parameters.setCloseable(true);
        parameters.setName(TOOLBOX_REFERENCE);

        ActionCommands dockingActions = new ActionCommands();
        parameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(parameters));
        dockingActions.addAction(
                new DefaultAction("ACTION_REFRESH",
                    "ACTION_REFRESH",
                    ToolBoxIcon.getIcon("refresh"),
                    EventHandler.create(ActionListener.class, this, "refreshTree"))
        );

        openEditorList = new ArrayList<>();
        lee = new LogEditableElement();
        le = null;

        refreshTree();
    }

    public void refreshTree(){
        for(ProcessIdentifier pi : wpsService.getCapabilities()) {
            toolBoxPanel.addLocalSource(pi);
        }
        toolBoxPanel.refreshAll();
    }

    public LocalWpsService getWpsService(){
        return wpsService;
    }



    @Deactivate
    public void dispose() {
        //Removes all the EditorDockable that were added
        for (EditorDockable ed : openEditorList) {
            dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
        }
        openEditorList = new ArrayList<>();
        toolBoxPanel.dispose();
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
     * Open a file browser to find a local script folder and add it.
     * Used in an EvenHandler in view.ui.ToolBoxPanel
     */
    public void addNewLocalScript(){
        OpenFilePanel openFilePanel = new OpenFilePanel("ToolBox.AddSource", "Add a source");
        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFilePanel)){
            addLocalSource(openFilePanel.getSelectedFile().toURI());
        }
    }

    /**
     * Adds a folder as a local script source.
     * @param uri Folder URI where the script are located.
     */
    public void addLocalSource(URI uri){
        addLocalSource(uri, null, false);
    }

    /**
     * Adds a folder as a local script source.
     * @param uri Folder URI where the script are located.
     * @param iconName Name of the icon to use for this node.
     */
    public void addLocalSource(URI uri, String[] iconName, boolean isDefaultScript){
        File file = new File(uri);
        if(file.isFile()){
            ProcessIdentifier pi = wpsService.addLocalScript(file, iconName, isDefaultScript, new File(file.getParent()).getName());
            if(pi != null) {
                toolBoxPanel.addLocalSource(pi);
            }
        }
        else if(file.isDirectory()){
            toolBoxPanel.addFolder(file.toURI(), file.getParentFile().toURI());
            for (File f : file.listFiles()) {
                if(f.isFile()){
                    ProcessIdentifier pi = wpsService.addLocalScript(f, iconName, isDefaultScript, new File(file.getParent()).getName());
                    if(pi != null) {
                        toolBoxPanel.addLocalSource(pi);
                    }
                }
                else if(f.isDirectory()){
                    toolBoxPanel.addFolder(f.toURI(), f.getParentFile().toURI());
                }
            }
        }
    }

    /**
     * Open the process window for the selected process.
     * @param scriptUri Script URI to execute as a process.
     * @return The ProcessEditableElement which contains the running process information (log, state, ...).
     */
    public ProcessEditableElement openProcess(URI scriptUri){
        Process process = wpsService.describeProcess(scriptUri);
        ProcessEditableElement pee = new ProcessEditableElement(process);
        pe = new ProcessEditor(this, pee);
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
            LoggerFactory.getLogger(WpsClientImpl.class).warn("The process '"+pee.getProcess().getTitle()+"' is already open.");
        }
        return pee;
    }

    public void openProcess(){
        openProcess(toolBoxPanel.getSelectedNode().getUri());
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
        return wpsService.checkProcess(uri);
    }

    /**
     * Verify if the given file is a well formed script.
     * @param uri URI to check.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkFolder(URI uri){
        return wpsService.checkFolder(uri);
    }

    /**
     * Remove the selected process in the tree.
     */
    public void removeProcess(URI uri){
        wpsService.removeProcess(uri);
    }

    /**
     * Returns the DataUIManager.
     * @return The DataUIManager.
     */
    public DataUIManager getDataUIManager(){
        return dataUIManager;
    }


    @Reference
    public void setLocalWpsService(LocalWpsService wpsService) {
        this.wpsService = wpsService;
    }
    public void unsetLocalWpsService(LocalWpsService wpsService) {
        this.wpsService = null;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        WpsClientImpl.dataManager = dataManager;
    }
    public void unsetDataManager(DataManager dataManager) {
        WpsClientImpl.dataManager = null;
    }
    public DataManager getDataManager(){
        return dataManager;
    }

    @Reference
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    public void unsetExecutorService(ExecutorService executorService) {
        this.executorService = null;
    }
    public ExecutorService getExecutorService(){
        return executorService;
    }

    @Reference
    public void setDockingManager(DockingManager dockingManager) {
        this.dockingManager = dockingManager;
    }
    public void unsetDockingManager(DockingManager dockingManager) {
        this.dockingManager = null;
    }

    public ProcessEditor getProcessEditor() {
        return pe;
    }

    /**
     * Loads the given URI by giving it to the registered WPSService and returns the name of the table containing
     * the file data.
     * To avoid to lock the user interface, start a SwingWorker which display a waiting layer in the ProcessEditor.
     * If the file can't be load, return null.
     * @param uri Uri of the data source file to load in the data base.
     * @param loadSource True if the source should be fully loaded in the base, false if a lighter loaded should be
     *                   done (like LINKED TABLE for H2)
     * @param inputOrOutput Input or Output (instance of DescriptionType class) asking for the data loading.
     * @return The table name corresponding to the loaded file in the data base or null if the file can't be loaded.
     */
    public String loadURI(URI uri, boolean loadSource, DescriptionType inputOrOutput) {
        //First retrieve the process containing the given input or output
        Process process = getWpsService().describeProcess(inputOrOutput.getIdentifier());
        //Find which of the open editors corresponds to the process
        for(EditorDockable ed : openEditorList){
            //Check if the editor is a ProcessEditor
            if(ed instanceof ProcessEditor){
                ProcessEditor pe = (ProcessEditor) ed;
                //Check if the editor corresponds to the process
                if(pe.getEditableElement().getObject().equals(process)) {
                    String tableName;
                    //Start the waiting layer
                    pe.startWaiting();
                    //Load the file and retrieve the table name
                    tableName = getWpsService().loadURI(uri, loadSource);
                    //Stop the waiting layer
                    pe.endWaiting();
                    return tableName;
                }
            }
        }
        //Return null if the file can't be loaded
        return null;
    }

    /**
     * Cancel the loading of the given URI.
     * @param uri Uri of the file being load to cancel.
     */
    public void cancelLoadURI(URI uri){
        getWpsService().cancelLoadUri(uri);
    }

    /**
     * Opens if the JTree the given tags.
     * @param tags List of tag to open.
     */
    public void openTags(List<String> tags){
        for(String tag : tags){
            toolBoxPanel.openNode(tag, ToolBoxPanel.TAG_MODEL);
        }
    }
}
