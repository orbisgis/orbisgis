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

package org.orbisgis.orbistoolbox.view.ui.dataui;

import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.ComplexeData.RawData;
import org.orbisgis.orbistoolbox.model.ComplexeData.ShapeFileData;
import org.orbisgis.orbistoolbox.model.Process;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class managing the link between data class (LiteralData, ComplexData ...) and the UI used to configure the inputs and the outputs.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataUIManager {

    /** Map linking the data class and its UI*/
    private Map<Class<? extends DataDescription>, DataUI> dataUIMap;

    /**
     * Main constructor.
     */
    public DataUIManager(){
        dataUIMap = new HashMap<>();
        linkClassUI(LiteralData.class, new LiteralDataUI());
        linkClassUI(RawData.class, new RawDataUI());
        linkClassUI(ShapeFileData.class, new ShapeFileUI());
    }

    /**
     * Link a class and its UI.
     * @param clazz Class to link.
     * @param dataUI UI corresponding to the class.
     */
    public void linkClassUI(Class<? extends DataDescription> clazz, DataUI dataUI){
        dataUIMap.put(clazz, dataUI);
    }

    /**
     * Returns the dataUI corresponding to the given class.
     * @param clazz data class.
     * @return DataUI of the given data class.
     */
    public DataUI getDataUI(Class<? extends DataDescription> clazz) {
        return dataUIMap.get(clazz);
    }

    public Map<URI, Object> getInputDefaultValues(Process process){
        Map<URI, Object> map = new HashMap<>();
        for(Input input : process.getInput()) {
            //If there is a DataUI corresponding to the input, get the defaults values.
            if(getDataUI(input.getDataDescription().getClass()) != null) {
                map.putAll(getDataUI(input.getDataDescription().getClass()).getDefaultValue(input));
            }
        }
        return map;
    }

    public Map<URI, Object> getOutputDefaultValues(Process process){
        Map<URI, Object> map = new HashMap<>();
        for(Output output : process.getOutput()) {
            map.putAll(getDataUI(output.getDataDescription().getClass()).getDefaultValue(output));
        }
        return map;
    }
}
