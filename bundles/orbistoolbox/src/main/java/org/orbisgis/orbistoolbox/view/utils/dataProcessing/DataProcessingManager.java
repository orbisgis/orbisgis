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

import org.orbisgis.orbistoolbox.model.DataDescription;
import org.orbisgis.orbistoolbox.model.DescriptionType;
import org.orbisgis.orbistoolbox.model.Input;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolbox.view.ToolBox;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to manage the DataProcessing classes.
 *
 * @author Sylvain PALOMINOS
 */
public class DataProcessingManager {

    private List<DataProcessing> listDataProcessing;

    private ToolBox toolBox;

    public DataProcessingManager(ToolBox toolBox){
        this.toolBox = toolBox;
        listDataProcessing = new ArrayList<>();
        listDataProcessing.add(new DataStoreProcessing());
    }

    public void preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap){
        for(DataProcessing dp : listDataProcessing){
            if(inputOrOutput instanceof Input) {
                DataDescription dataDescription = ((Input)inputOrOutput).getDataDescription();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    dp.preProcessData(toolBox, inputOrOutput, dataMap);
                }
            }
            if(inputOrOutput instanceof Output) {
                DataDescription dataDescription = ((Output)inputOrOutput).getDataDescription();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    dp.preProcessData(toolBox, inputOrOutput, dataMap);
                }
            }
        }
    }

    public void postProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap){
        for(DataProcessing dp : listDataProcessing){
            if(inputOrOutput instanceof Input) {
                DataDescription dataDescription = ((Input) inputOrOutput).getDataDescription();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    dp.postProcessData(toolBox, inputOrOutput, dataMap);
                }
            }
            if(inputOrOutput instanceof Output) {
                DataDescription dataDescription = ((Output) inputOrOutput).getDataDescription();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    dp.postProcessData(toolBox, inputOrOutput, dataMap);
                }
            }
        }
    }
}
