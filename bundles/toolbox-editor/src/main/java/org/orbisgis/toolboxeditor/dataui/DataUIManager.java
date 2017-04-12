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

package org.orbisgis.toolboxeditor.dataui;

import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.DataDescriptionType;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.model.*;
import org.orbiswps.server.model.BoundingBoxData;

import javax.swing.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class managing the link between data class (LiteralData, ComplexData ...) and the UI used to configure the inputs and the outputs.
 *
 * @author Sylvain PALOMINOS
 * @author Erwan Bocher
 **/

public class DataUIManager {

    /** Map linking the data class and its UI*/
    private Map<Class<? extends DataDescriptionType>, DataUI> dataUIMap;

    /**
     * Main constructor.
     */
    public DataUIManager(WpsClientImpl wpsClient){
        dataUIMap = new HashMap<>();
        linkClassUI(LiteralDataType.class, new LiteralDataUI(), wpsClient);
        linkClassUI(RawData.class, new RawDataUI(), wpsClient);
        linkClassUI(JDBCTable.class, new JDBCTableUI(), wpsClient);
        linkClassUI(JDBCColumn.class, new JDBCColumnUI(), wpsClient);
        linkClassUI(JDBCValue.class, new JDBCValueUI(), wpsClient);
        linkClassUI(Enumeration.class, new EnumerationUI(), wpsClient);
        linkClassUI(GeometryData.class, new GeometryUI(), wpsClient);
        linkClassUI(Password.class, new PasswordUI(), wpsClient);
        linkClassUI(BoundingBoxData.class, new BoundingBoxDataUI(), wpsClient);
    }

    /**
     * Link a class and its UI.
     * @param clazz Class to link.
     * @param dataUI UI corresponding to the class.
     */
    public void linkClassUI(Class<? extends DataDescriptionType> clazz, DataUI dataUI, WpsClientImpl wpsClient){
        dataUI.setWpsClient(wpsClient);
        dataUIMap.put(clazz, dataUI);
    }

    /**
     * Returns the dataUI corresponding to the given class.
     * @param clazz data class.
     * @return DataUI of the given data class.
     */
    public DataUI getDataUI(Class<? extends DataDescriptionType> clazz) {
        return dataUIMap.get(clazz);
    }

    /**
     * Returns a Map of the defaults input values of a process and their identifier URI.
     * @param process Process to analyse
     * @return Map of the default input values and their URI.
     */
    public Map<URI, Object> getInputDefaultValues(ProcessDescriptionType process){
        Map<URI, Object> map = new HashMap<>();
        for(InputDescriptionType input : process.getInput()) {
            //If there is a DataUI corresponding to the input, get the defaults values.
            if(getDataUI(input.getDataDescription().getValue().getClass()) != null) {
                map.putAll(getDataUI(input.getDataDescription().getValue().getClass()).getDefaultValue(input));
            }
        }
        return map;
    }

    /**
     * Return the Icon associated to the data represented by the given input or output.
     * @param inputOrOutput Input or Output to analyse.
     * @return An ImageIcon corresponding to the data type.
     */
    public ImageIcon getIconFromData(net.opengis.wps._2_0.DescriptionType inputOrOutput) {
        DataDescriptionType dataDescription = null;
        if(inputOrOutput instanceof InputDescriptionType){
            dataDescription = ((InputDescriptionType) inputOrOutput).getDataDescription().getValue();
        }
        if(inputOrOutput instanceof OutputDescriptionType){
            dataDescription = ((OutputDescriptionType) inputOrOutput).getDataDescription().getValue();
        }
        ImageIcon icon = dataUIMap.get(dataDescription.getClass()).getIconFromData(inputOrOutput);
        if(icon != null) {
            return icon;
        }
        return ToolBoxIcon.getIcon(ToolBoxIcon.UNDEFINED);
    }
}
