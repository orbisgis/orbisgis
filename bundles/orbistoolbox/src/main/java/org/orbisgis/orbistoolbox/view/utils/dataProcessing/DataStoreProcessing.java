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

package org.orbisgis.orbistoolbox.view.utils.dataProcessing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.orbisgis.orbistoolbox.model.DescriptionType;
import org.orbisgis.orbistoolbox.model.DataStore;
import org.orbisgis.orbistoolbox.model.Input;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public class DataStoreProcessing implements DataProcessing {

    private Map<URI, Object> stash;

    public DataStoreProcessing(){
        stash = new HashMap<>();
    }

    @Override
    public Class getDataClass() {
        return DataStore.class;
    }

    @Override
    public Object preProcessData(ToolBox toolBox, DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        URI uri = inputOrOutput.getIdentifier();
        URI dataStoreURI = (URI)dataMap.get(uri);
        String tableName = null;
        if(inputOrOutput instanceof Input){
            if(dataStoreURI.getScheme().equals("geocatalog")){
                tableName = dataStoreURI.getSchemeSpecificPart();
                dataMap.put(uri, tableName);
            }
            else if(dataStoreURI.getScheme().equals("file")){
                tableName = toolBox.loadFile(new File(dataStoreURI.getPath()));
                dataMap.put(uri, tableName);
            }
        }
        if(inputOrOutput instanceof Output){
            if(dataStoreURI.getScheme().equals("geocatalog")){
                stash.put(uri, dataMap.get(uri));
                tableName = dataStoreURI.getSchemeSpecificPart();
                dataMap.put(uri, tableName);
            }
            else if(dataStoreURI.getScheme().equals("file")){
                stash.put(uri, dataMap.get(uri));
                tableName = toolBox.loadFile(new File(dataStoreURI.getPath()));
                dataMap.put(uri, tableName);
            }
        }
        return tableName;
    }

    @Override
    public Object postProcessData(ToolBox toolBox, DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        if(inputOrOutput instanceof Input){

        }
        if(inputOrOutput instanceof Output){
            URI uri = inputOrOutput.getIdentifier();
            URI dataStoreURI = (URI)stash.get(uri);
            if(dataStoreURI.getScheme().equals("file")){
                toolBox.saveFile(new File(dataStoreURI), dataMap.get(uri).toString());
            }
        }
        return null;
    }
}
