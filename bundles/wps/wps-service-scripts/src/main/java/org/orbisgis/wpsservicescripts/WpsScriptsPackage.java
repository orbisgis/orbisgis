package org.orbisgis.wpsservicescripts;

import net.opengis.ows._2.CodeType;
import org.apache.commons.io.IOUtils;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wpsservice.LocalWpsServer;

import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsclient.WpsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * In the WpsService, the script are organized in a tree, which has the WpsService as root.
 *
 * Scripts can be add to the tree under a specific node path with custom icon with the following method :
 *      localWpsService.addLocalScript('processFile', 'icon', 'boolean', 'nodePath');
 * with the following parameter :
 *      processFile : The File object corresponding to the script. Be careful, the plugin resource files can't be
 *              accessed from the outside of the plugin. So you have to copy it (in a temporary file as example) before
 *              adding it to the WpsService.
 *      icon : Array of Icon object to use for the WpsClient tree containing the processes. The first icon will be used
 *              for the first node of the path, the second icon for the second node ... If the node already exists,
 *              its icon won't be changed. If there is less icon than node, the last icon will be used for the others.
 *              If no icon are specified, the default one from the WpsClient will be used.
 *      boolean : it SHOULD be true. Else the used will be able to remove the process from the WpsClient without
 *              deactivating the plugin.
 *      nodePath : Path to the node where the process should be add. If nodes of the path doesn't exists, they will be
 *              created.
 * This add method return a ProcessIdentifier object which give all the information needed to identify a process. It
 * should be kept to be able to remove it later.
 *
 *
 * The 'customLoadScript()' method load the scripts one by one under different file path with  different icons.
 *
 *
 * When the plugin is launched , the 'activate()' method is call. This method load the scripts in the
 * WpsService and refresh the WpsClient.
 * When the plugin is stopped or uninstalled, the 'deactivate()' method is called. This method removes the loaded script
 * from the WpsService and refreshes the WpsClient.
 *
 */
public class WpsScriptsPackage {

    /**
     * Logger instance.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(WpsScriptsPackage.class);

    /**
     * OrbisGIS core workspace.
     */
    protected CoreWorkspace coreWorkspace;

    /**
     * The WPS service of OrbisGIS.
     * The WPS service contains all the declared processes available for the client (in OrbisGIS the toolbox).
     */
    protected LocalWpsServer localWpsService;

    /**
     * The WPS client of OrbisGIS.
     */
    protected WpsClient wpsClient;

    /**
     * List of identifier of the processes loaded by this plusgin.
     */
    protected List<CodeType> listIdProcess;

    /**
     * This method loads the scripts one by one under different node path with different icons.
     * (Be careful before any modification)
     */
    protected void customLoadScript(String processpath, String[] icons, String path){
        String tempFolderPath = coreWorkspace.getApplicationFolder();
        File tempFolder = new File(tempFolderPath, "wpsscripts");
        if(!tempFolder.exists()) {
            if(!tempFolder.mkdirs()){
                LOGGER.error("Unable to create the OrbisGIS temporary folder.");
                return;
            }
        }
        URL scriptUrl = this.getClass().getResource(processpath);
        final File tempFile = new File(tempFolder.getAbsolutePath(), new File(scriptUrl.getFile()).getName());
        if(!tempFile.exists()) {
            try{
                if(tempFile.createNewFile()){
                    LOGGER.error("Unable to create the script file.");
                    return;
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(WpsScriptsPackage.class).error(e.getMessage());
            }
        }
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(scriptUrl.openStream(), out);
        }
        catch (Exception e){
            LOGGER.error("Unable to copy the content of the script to the temporary file.");
            return;
        }
        List<ProcessIdentifier> piList = localWpsService.addLocalSource(tempFile,
                icons,
                false,
                path);
        for(ProcessIdentifier pi : piList){
            listIdProcess.add(pi.getProcessDescriptionType().getIdentifier());
        }
    }

    /**
     * This method removes all the scripts contained in the 'listIdProcess' list. (Be careful before any modification)
     */
    protected void removeAllScripts(){
        for(CodeType idProcess : listIdProcess){
            localWpsService.removeProcess(idProcess);
        }
    }

    /**
     * This method copy the an icon into the temporary system folder to make it accessible by the WpsClient
     */
    protected String loadIcon(String iconName){
        URL iconUrl = this.getClass().getResource("icons/"+iconName);
        String tempFolderPath = coreWorkspace.getApplicationFolder();
        File tempFolder = new File(tempFolderPath, "wpsscripts");
        if(!tempFolder.exists()) {
            if(!tempFolder.mkdirs()){
                LOGGER.error("Unable to create the OrbisGIS temporary folder.");
                return null;
            }
        }
        //Create a temporary File object
        final File tempFile = new File(tempFolder.getAbsolutePath(), iconName);
        if(!tempFile.exists()) {
            try{
                if(tempFile.createNewFile()){
                    LOGGER.error("Unable to create the icon file.");
                    return null;
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(WpsScriptsPackage.class).error(e.getMessage());
            }
        }
        //Copy the content of the resource file in the temporary file.
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(iconUrl.openStream(), out);
        }
        catch (Exception e){
            LOGGER.error("Unable to copy the content of the icon to the temporary file.");
            return null;
        }
        return tempFile.getAbsolutePath();
    }
}
