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

package org.orbisgis.wpsservice.controller.execution;

import net.opengis.wps._2_0.DataDescriptionType;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;

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

    public DataProcessingManager(){
        listDataProcessing = new ArrayList<>();
        listDataProcessing.add(new GeometryProcessing());
    }

    public Map<URI, Object> preProcessData(DescriptionType input, Map<URI, Object> dataMap,
                                           ProcessExecutionListener pel){
        Map<URI, Object> stash = new HashMap<>();
        for(DataProcessing dp : listDataProcessing){
            if(input instanceof InputDescriptionType) {
                DataDescriptionType dataDescription = ((InputDescriptionType)input).getDataDescription().getValue();
                if (dp.getDataClass().isAssignableFrom(dataDescription.getClass())) {
                    stash.putAll(dp.preProcessData(input, dataMap, pel));
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
