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

import org.orbisgis.wpsservice.WpsServiceImplementation;
import org.orbisgis.wpsservice.model.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public class DataStoreProcessing implements DataProcessing {

    @Override
    public Class<? extends DataDescription> getDataClass() {
        return DataStore.class;
    }

    @Override
    public Map<URI, Object> preProcessData(WpsServiceImplementation wpsServiceImplementation, DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        Map<URI, Object> stash = new HashMap<>();
        URI uri = inputOrOutput.getIdentifier();
        URI dataStoreURI = (URI)dataMap.get(uri);
        String tableName;
        if(inputOrOutput instanceof Input){
            if(dataStoreURI.getScheme().equals("geocatalog")){
                tableName = dataStoreURI.getSchemeSpecificPart();
                dataMap.put(uri, tableName);
                stash.put(uri, "geocatalog");
            }
            else if(dataStoreURI.getScheme().equals("file")){
                DataStore dataStore = (DataStore) ((Input) inputOrOutput).getDataDescription();
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
                        WpsServiceImplementation.removeTempTable(dataStoreURI.getFragment());
                    }
                    else{
                        path = path.replace("$", "");
                    }
                    dataMap.put(uri, path);
                }
            }
        }
        if(inputOrOutput instanceof Output){
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
    public void postProcessData(WpsServiceImplementation wpsServiceImplementation, DescriptionType inputOrOutput,
                                Map<URI, Object> dataMap, Map<URI, Object> stash) {
        if(inputOrOutput instanceof Input){
            URI uri = inputOrOutput.getIdentifier();
            if(stash.get(uri) != null && stash.get(uri).equals("file")){
                WpsServiceImplementation.removeTempTable(dataMap.get(uri).toString());
            }
        }
        if(inputOrOutput instanceof Output){
            URI uri = inputOrOutput.getIdentifier();
            URI dataStoreURI = (URI)stash.get(uri);
            if(dataStoreURI.getScheme().equals("file")){
                wpsServiceImplementation.saveURI(URI.create(dataStoreURI.getScheme()+":"+dataStoreURI.getSchemeSpecificPart()), dataMap.get(uri).toString());
            }
        }
    }
}
