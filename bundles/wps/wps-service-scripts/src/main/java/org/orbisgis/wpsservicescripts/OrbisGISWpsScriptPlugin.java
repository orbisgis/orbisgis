package org.orbisgis.wpsservicescripts;

import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wpsservice.LocalWpsServer;
import org.orbisgis.wpsclient.WpsClient;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Main class of the plugin which declares the scripts to add, their locations in the process tree and the icons
 * associated.
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
@Component(immediate = true)
public class OrbisGISWpsScriptPlugin extends WpsScriptsPackage {

    /**
     * OSGI method used to give to the plugin the WpsService. (Be careful before any modification)
     * @param localWpsService
     */
    @Reference
    public void setLocalWpsService(LocalWpsServer localWpsService) {
        this.localWpsService = localWpsService;
    }

    /**
     * OSGI method used to remove from the plugin the WpsService. (Be careful before any modification)
     * @param localWpsService
     */
    public void unsetLocalWpsService(LocalWpsServer localWpsService) {
        this.localWpsService = null;
    }

    /**
     * OSGI method used to give to the plugin the WpsClient. (Be careful before any modification)
     * @param wpsClient
     */
    @Reference
    public void setWpsClient(WpsClient wpsClient) {
        this.wpsClient = wpsClient;
    }

    /**
     * OSGI method used to remove from the plugin the WpsClient. (Be careful before any modification)
     * @param wpsClient
     */
    public void unsetWpsClient(WpsClient wpsClient) {
        this.wpsClient = null;
    }

    /**
     * OSGI method used to give to the plugin the CoreWorkspace. (Be careful before any modification)
     * @param coreWorkspace
     */
    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }

    /**
     * OSGI method used to remove from the plugin the CoreWorkspace. (Be careful before any modification)
     * @param coreWorkspace
     */
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    /**
     * This methods is called once the plugin is loaded.
     *
     * It first check if the WpsService is ready.
     * If it is the case:
     *      Load the processes in the WpsService and save their identifier in the 'listIdProcess' list.
     *      Check if the WpsClient is ready.
     *      If it is the case :
     *          Refresh the WpsClient to display the processes.
     *      If not :
     *          Warn the user in the log that the WpsClient could not be found.
     * If not :
     *      Log the error and skip the process loading.
     *
     * In this class there is two methods to add the scripts :
     * The default one :
     *      This method adds all the scripts of the contained by the 'scripts' resources folder under the specified
     *      'nodePath' in the WpsClient. It keeps the file tree structure.
     * The custom one :
     *      This methods adds each script one by one under a specific node for each one.
     */
    @Activate
    public void activate(){
        listIdProcess = new ArrayList<>();
        //Check the WpsService
        if(localWpsService != null){
            //Default method to load the scripts
            String[] icons = loadIcons(new String[]{"orbisgis.png"});
            customLoadScript("scripts/IO/csvToPointLayer.groovy", icons, "OrbisGIS/IO");
            customLoadScript("scripts/Network/createGraph.groovy", icons, "OrbisGIS/Network");
            customLoadScript("scripts/Table/deleteRows.groovy", icons, "OrbisGIS/Table");
            customLoadScript("scripts/Table/insertValues.groovy", icons, "OrbisGIS/Table");
            customLoadScript("scripts/Table/joinTables.groovy", icons, "OrbisGIS/Table");
            customLoadScript("scripts/Vector/Convert/extractCenter.groovy", icons, "OrbisGIS/Vector/Convert");
            customLoadScript("scripts/Vector/Create/createGridOfPoints.groovy", icons, "OrbisGIS/Vector/Create");
            customLoadScript("scripts/Vector/Create/createGridOfPolygons.groovy", icons, "OrbisGIS/Vector/Create");
            customLoadScript("scripts/Vector/Create/fixedExtrudePolygons.groovy", icons, "OrbisGIS/Vector/Create");
            customLoadScript("scripts/Vector/Create/variableExtrudePolygons.groovy", icons, "OrbisGIS/Vector/Create");
            customLoadScript("scripts/Vector/Operators/fixedDistanceBuffer.groovy", icons, "OrbisGIS/Vector/Operators");
            customLoadScript("scripts/Vector/Operators/variableDistanceBuffer.groovy", icons, "OrbisGIS/Vector/Operators");
            customLoadScript("scripts/Vector/Properties/geometryProperties.groovy", icons, "OrbisGIS/Vector/Properties");
            customLoadScript("scripts/Vector/Transform/reprojectGeometries.groovy", icons, "OrbisGIS/Vector/Transform");

            //Check the WpsClient
            if(wpsClient != null){
                //Refresh the client
                wpsClient.refreshAvailableScripts();
            }
        }
        else{
            LoggerFactory.getLogger(WpsScriptsPackage.class).error(
                    "Unable to retrieve the WpsService from OrbisGIS.\n" +
                            "The processes won't be loaded.");
        }
    }

    /**
     * This method is called when the plugin is deactivated.
     * If the WpsService is ready, removes all the previously loaded scripts.
     * If not, log the error and skip the process removing.
     * Then if the WpsClient is ready, refresh it.
     */
    @Deactivate
    public void deactivate(){
        if(localWpsService != null) {
            removeAllScripts();
            if(wpsClient != null) {
                wpsClient.refreshAvailableScripts();
            }
        }
        else{
            LoggerFactory.getLogger(WpsScriptsPackage.class).error(
                    "Unable to retrieve the WpsService from OrbisGIS.\n" +
                            "The processes won't be removed.");
        }
    }
}
