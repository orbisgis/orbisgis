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

package org.orbisgis.wpsservice.controller.execution;

import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.DescriptionType;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener.LogType;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public class DataStoreProcessing implements DataProcessing {

    /**Logger */
    private Logger LOGGER = LoggerFactory.getLogger(DataStoreProcessing.class);

    private LocalWpsService wpsService;

    public DataStoreProcessing(LocalWpsService wpsService){
        setLocalWpsService(wpsService);
    }

    public void setLocalWpsService(LocalWpsService wpsService){
        this.wpsService = wpsService;
    }

    @Override
    public Class<? extends DataDescriptionType> getDataClass() {
        return DataStore.class;
    }

    @Override
    public Map<URI, Object> preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap,
                                           ProcessExecutionListener pel) {
        Map<URI, Object> stash = new HashMap<>();
        URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
        URI dataStoreURI = (URI)dataMap.get(uri);
        String tableName;
        if(inputOrOutput instanceof InputDescriptionType){
            if(dataStoreURI.getScheme().equals("geocatalog")){
                tableName = dataStoreURI.getSchemeSpecificPart();
                dataMap.put(uri, tableName);
                stash.put(uri, "geocatalog");
            }
            else if(dataStoreURI.getScheme().equals("file")){
                DataStore dataStore = (DataStore) ((InputDescriptionType) inputOrOutput).getDataDescription().getValue();
                String path = dataStoreURI.getSchemeSpecificPart();
                boolean keep = path.endsWith("$");
                if(dataStore.isAutoImport()) {
                    dataMap.put(uri, dataStoreURI.getFragment());
                    if(!keep) {
                        stash.put(uri, "file");
                    }
                }
                else{
                    if(!keep) {
                        wpsService.removeTempTable(dataStoreURI.getFragment());
                    }
                    else{
                        path = path.replace("$", "");
                    }
                    dataMap.put(uri, path);
                }
            }
        }
        if(inputOrOutput instanceof OutputDescriptionType){
            if(dataStoreURI.getScheme().equals("geocatalog")){
                stash.put(uri, dataStoreURI);
                tableName = dataStoreURI.getSchemeSpecificPart();
                dataMap.put(uri, tableName);
            }
            else if(dataStoreURI.getScheme().equals("file")){
                stash.put(uri, dataStoreURI);
                tableName = dataStoreURI.getFragment();
                dataMap.put(uri, tableName);
            }
        }
        return stash;
    }

    @Override
    public void postProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Map<URI, Object> stash,
                                ProcessExecutionListener pel) {
        if(inputOrOutput instanceof InputDescriptionType){
            URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
            if(stash.get(uri) != null && stash.get(uri).equals("file")){
                wpsService.removeTempTable(dataMap.get(uri).toString());
            }
        }
        if(inputOrOutput instanceof OutputDescriptionType){
            URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
            URI dataStoreURI = (URI)stash.get(uri);
            if(dataStoreURI.getScheme().equals("file")){
                String path = dataStoreURI.getSchemeSpecificPart();
                boolean keep = path.endsWith("$");
                path = path.replace("$", "");
                wpsService.saveURI(URI.create(dataStoreURI.getScheme()+":"+path), dataMap.get(uri).toString());
                if(pel != null) {
                    pel.appendLog(LogType.INFO, "Table '" + dataMap.get(uri).toString() + "' successfully exported into '" +
                            path + "'.");
                }
                if(!keep){
                    wpsService.removeTempTable(dataMap.get(uri).toString());
                }
            }
            else if(dataStoreURI.getScheme().equals("geocatalog")){
                if(pel != null) {
                    pel.appendLog(LogType.INFO, "Table '" + dataMap.get(uri).toString() + "' successfully created.");
                }
            }
        }
    }
}
