package org.orbisgis.wpsservicescripts;

import net.opengis.ows._2.CodeType;
import org.apache.commons.io.IOUtils;
import org.orbisgis.wpsservice.LocalWpsServer;

import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsclient.WpsClient;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
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
 * There is two method already implemented in this class to add the processes :
 *      Default method : the 'defaultLoadScript('nodePath')' methods which take as argument the nodePath and adds all
 *              the scripts from the 'resources' folder, keeping the file tree structure. All the script have the same
 *              icons.
 *      Custom method : the 'customLoadScript()' method load the scripts one by one under different file path with
 *              different icons.
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
     * This method load the script contained in the 'scripts' folder of the resources an put the under the given a
     * node with the given name. (Be careful before any modification)
     *
     * @param nodePath Name of the node containing the loaded scripts.
     */
    protected void defaultLoadScript(String nodePath, String[] iconArrayName){
        //Retrieve the 'scripts' folder path.
        String folderPath = this.getClass().getResource("scripts").getFile();
        //Load the icon and get back the corresponding array with file path (in the same order)
        String[] array = loadIcons(iconArrayName);
        //Recursively add the scripts.
        recursiveScriptLoad(nodePath, folderPath, array);

    }

    protected void recursiveScriptLoad(String nodePath, String folderPath, String[] iconArrayName){
        //Get the URL of all the files contained in the 'script' folder.
        Enumeration<URL> enumUrl = FrameworkUtil.getBundle(this.getClass()).findEntries(folderPath, "*", false);
        //For each url, if it is a file, load it, if it is a directory, check its content.
        while(enumUrl.hasMoreElements()){
            try {
                //Get the URL
                URL scriptUrl = enumUrl.nextElement();
                //If the url if a groovy file,
                if(scriptUrl.getFile().endsWith(".groovy")) {
                    //Create a temporary File object
                    final File tempFile = File.createTempFile("wpsprocess", ".groovy");
                    tempFile.deleteOnExit();
                    //Copy the content of the resource file in the temporary file.
                    try (FileOutputStream out = new FileOutputStream(tempFile)) {
                        IOUtils.copy(scriptUrl.openStream(), out);
                    }
                    //Add the temporary file to the WpsService an get back the ProcessIdentifier object
                    List<ProcessIdentifier> piList = localWpsService.addLocalSource(tempFile, iconArrayName, false, nodePath);
                    //Save the process id to be able to remove the process later.
                    for(ProcessIdentifier pi : piList) {
                        listIdProcess.add(pi.getProcessDescriptionType().getIdentifier());
                    }
                }
                //If the url is a folder,
                else if(!scriptUrl.getFile().contains(".")){
                    String folderName = new File(scriptUrl.getFile()).getName();
                    //Recursively add the scripts.
                    recursiveScriptLoad(nodePath+"/"+folderName, folderPath+File.separator+folderName, iconArrayName);
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(WpsScriptsPackage.class).error(e.getMessage());
            }
        }
    }

    /**
     * This method loads the scripts one by one under different node path with different icons.
     * (Be careful before any modification)
     */
    protected void customLoadScript(String processpath, String[] icons, String path){
        try {
            URL scriptUrl = this.getClass().getResource(processpath);
            final File tempFile = File.createTempFile("wpsprocess", ".groovy");
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(scriptUrl.openStream(), out);
            }
            List<ProcessIdentifier> piList = localWpsService.addLocalSource(tempFile,
                    icons,
                    false,
                    path);
            for(ProcessIdentifier pi : piList){
                listIdProcess.add(pi.getProcessDescriptionType().getIdentifier());
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(WpsScriptsPackage.class).error(e.getMessage());
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
     * This method copy the icons into the temporary system folder to make them accessible by the WpsClient
     */
    protected String[] loadIcons(String[] iconArrayName){
        String iconPath = this.getClass().getResource("icons").getFile();
        String[] names = new String[iconArrayName.length];
        Enumeration<URL> enumUrl = FrameworkUtil.getBundle(this.getClass()).findEntries(iconPath, "*", false);
        while(enumUrl.hasMoreElements()){
            try {
                //Get the URL
                URL scriptUrl = enumUrl.nextElement();
                File f = new File(scriptUrl.getFile());
                //If the url if a png file,
                if(scriptUrl.getFile().endsWith(".png")) {
                    //Create a temporary File object
                    final File tempFile = File.createTempFile("wpsprocessicon", ".png");
                    tempFile.deleteOnExit();
                    //Copy the content of the resource file in the temporary file.
                    try (FileOutputStream out = new FileOutputStream(tempFile)) {
                        IOUtils.copy(scriptUrl.openStream(), out);
                    }
                    for(int i=0; i<iconArrayName.length; i++){
                        if(iconArrayName[i].equals(f.getName())){
                            names[i] = tempFile.getAbsolutePath();
                        }
                    }
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(WpsScriptsPackage.class).error(e.getMessage());
            }
        }
        return names;
    }

    /**
     * This method copy the an icon into the temporary system folder to make it accessible by the WpsClient
     */
    protected String loadIcon(String iconName){
        URL iconUrl = this.getClass().getResource("icons"+File.separator+iconName);
        try {
            //Create a temporary File object
            final File tempFile = File.createTempFile("wpsprocessicon", ".png");
            tempFile.deleteOnExit();
            //Copy the content of the resource file in the temporary file.
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(iconUrl.openStream(), out);
            }
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            LoggerFactory.getLogger(WpsScriptsPackage.class).error(e.getMessage());
        }
        return null;
    }
}
