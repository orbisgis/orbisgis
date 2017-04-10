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
package org.orbisgis.orbiswpsservicescripts;

import junit.framework.Assert;
import net.opengis.ows._2.CodeType;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.GetCapabilitiesType;
import org.junit.Test;
import org.orbisgis.orbiswpsservice.WpsServer;
import org.orbisgis.orbiswpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.orbiswpsservice.utils.WpsServerListener;

import javax.sql.DataSource;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Class to test the well working of the plugin life cycle :
 *
 * On activating : add all the scripts of the resource folder (src/main/resources/org/orbisgis/wpsservicescripts/scripts)
 * to a fake WPS server {@code CustomWpsService}.
 *
 * On deactivating : remove all the scripts add from the WPS server.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsScriptPluginTest {

    /**
     * Test the life cycle of the plugin.
     */
    @Test
    public void testPluginLifeCycle(){
        //Initialize an instance of OrbisGISWpsScriptPlugin, CustomWpsService and CustomCoreWorkspace
        CustomWpsService localWpsServer = new CustomWpsService();
        localWpsServer.setScriptFolder(System.getProperty("java.io.tmpdir"));
        WpsScriptPlugin plugin = new WpsScriptPlugin();
        //Give to the OrbisGISWpsScriptPlugin the LocalWpsServer and the CoreWorkspace
        plugin.setWpsServer(localWpsServer);
        //Simulate the activation of the plugin and get back the list of script file add
        plugin.activate();
        List<File> addScriptList = localWpsServer.getAddScriptList();
        //Gets the list of the script files contained in the resource folder of the plugin
        File folder = new File(this.getClass().getResource("scripts").getFile());
        List<File> resourceGroovyScriptList = getAllGroovyScripts(folder);
        //Test if each file from the resource folder has been loaded in the WPS server
        for(File resourceScript : resourceGroovyScriptList){
            boolean isResourceScriptAdd = false;
            for(File addScript : addScriptList){
                if(resourceScript.getName().equals(addScript.getName())){
                    isResourceScriptAdd = true;
                }
            }
            Assert.assertTrue("The resource file '"+resourceScript.getName()+"' should be add by the plugin.", isResourceScriptAdd);
        }
        //Simulate the deactivation of the plugin
        plugin.deactivate();
        //Test if all the script have been removed
        Assert.assertTrue("All the scripts should have been removed from the server.",
                localWpsServer.getAddScriptList().isEmpty());
        //Unset the CoreWorkspace and the LocalWpsService
        plugin.unsetWpsServer(null);
    }

    /**
     * Returns the list of the groovy script file of a given directory.
     * @param directory Directory to explore.
     * @return The list of the groovy script files.
     */
    private List<File> getAllGroovyScripts(File directory){
        List<File> scriptList = new ArrayList<>();
        for(File f : directory.listFiles()) {
            if (f.isDirectory()) {
                scriptList.addAll(getAllGroovyScripts(f));
            }
            else {
                scriptList.add(f);
            }
        }
        return scriptList;
    }

    /**
     * A fake LocalWpsServer implementation. Only addLocalSource(File,String[],boolean,String) and removeProcess(URI)
     * methods are implemented. It is used to simulate a WpsServer but it only store in a list the loaded script.
     * This list is accessible throw the methods getAddScriptList().
     */
    private class CustomWpsService implements WpsServer {
        private List<File> addScriptList = new ArrayList<>();
        private String scriptFolder;

        @Override
        public List<ProcessIdentifier> addProcess(File f, String[] iconName, boolean isDefault, String nodePath) {
            addScriptList.add(f);
            //Building of an empty processOffering
            CodeType codeType = new CodeType();
            codeType.setValue(f.toURI().toString());
            ProcessDescriptionType processDescriptionType = new ProcessDescriptionType();
            processDescriptionType.setIdentifier(codeType);
            ProcessOffering processOffering = new ProcessOffering();
            processOffering.setProcess(processDescriptionType);
            //Return the ProcessIdentifier of the source to add
            List<ProcessIdentifier> processIdentifierList = new ArrayList<>();
            processIdentifierList.add(new ProcessIdentifier(processOffering, null, null, null));
            return processIdentifierList;
        }

        /**
         * Returns the list of the script files add to the server.
         * @return The list of the script files add to the server.
         */
        public List<File> getAddScriptList(){ return addScriptList;}

        @Override public void removeProcess(URI identifier) {
            File fileToRemove = null;
            for(File f : addScriptList){
                if(f.toURI().toString().equals(identifier.toString())){
                    fileToRemove = f;
                }
            }
            addScriptList.remove(fileToRemove);
        }
        @Override public String getScriptFolder() {return scriptFolder;}
        @Override public void setScriptFolder(String scriptFolder) {this.scriptFolder = scriptFolder;}


        //Methods not used in the tests
        @Override public void setDataSource(DataSource dataSource) {}
        @Override public void setExecutorService(ExecutorService executorService) {}
        @Override public void addGroovyProperties(Map<String, Object> propertiesMap) {}
        @Override public void removeGroovyProperties(Map<String, Object> propertiesMap) {}
        @Override public Object getCapabilities(GetCapabilitiesType getCapabilities) {return null;}
        @Override public ProcessOfferings describeProcess(DescribeProcess describeProcess) {return null;}
        @Override public Object execute(ExecuteRequestType execute) {return null;}
        @Override public StatusInfo getStatus(GetStatus getStatus) {return null;}
        @Override public Result getResult(GetResult getResult) {return null;}
        @Override public StatusInfo dismiss(Dismiss dismiss) {return null;}
        @Override public OutputStream callOperation(InputStream xml) {return null;}
        @Override public void cancelProcess(UUID jobId) {}
        @Override public Database getDatabase() {return null;}
        @Override public void setDatabase(Database database) {}
        @Override public void addWpsServerListener(WpsServerListener wpsServerListener) {}
        @Override public void removeWpsServerListener(WpsServerListener wpsServerListener) {}
    }
}
