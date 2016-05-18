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

import net.opengis.wps._2_0.DataDescriptionType;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.wpsservice.LocalWpsService;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to manage the DataProcessing classes.
 *
 * @author Sylvain PALOMINOS
 */
public class DataProcessingManager {

    private List<DataProcessing> listDataProcessing;

    public DataProcessingManager(LocalWpsService wpsService){
        listDataProcessing = new ArrayList<>();
        listDataProcessing.add(new DataStoreProcessing(wpsService));
        listDataProcessing.add(new LiteralDataProcessing());
        listDataProcessing.add(new GeometryProcessing());
        listDataProcessing.add(new RawDataProcessing());
    }

    public Map<URI, Object> preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap,
                                           ProcessExecutionListener pel){
        Map<URI, Object> stash = new HashMap<>();
        for(DataProcessing dp : listDataProcessing){
            if(inputOrOutput instanceof InputDescriptionType) {
                DataDescriptionType dataDescription = ((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    stash.putAll(dp.preProcessData(inputOrOutput, dataMap, pel));
                }
            }
            if(inputOrOutput instanceof OutputDescriptionType) {
                DataDescriptionType dataDescription = ((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    stash.putAll(dp.preProcessData(inputOrOutput, dataMap, pel));
                }
            }
        }
        return stash;
    }

    public void postProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Map<URI, Object> stash,
                                ProcessExecutionListener pel){
        for(DataProcessing dp : listDataProcessing){
            if(inputOrOutput instanceof InputDescriptionType) {
                DataDescriptionType dataDescription = ((InputDescriptionType) inputOrOutput).getDataDescription().getValue();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    dp.postProcessData(inputOrOutput, dataMap, stash, pel);
                }
            }
            if(inputOrOutput instanceof OutputDescriptionType) {
                DataDescriptionType dataDescription = ((OutputDescriptionType) inputOrOutput).getDataDescription().getValue();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    dp.postProcessData(inputOrOutput, dataMap, stash, pel);
                }
            }
        }
    }
}
