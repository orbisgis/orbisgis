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

package org.orbisgis.wpsservice.controller.dataprocessing;

import net.opengis.wps._2_0.DataDescriptionType;
import net.opengis.wps._2_0.DescriptionType;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;

import java.net.URI;
import java.util.Map;

/**
 * Interface to define the DataProcessing classes.
 * The method contained by this interface will be called before and after the process to adapt the input and
 * output data to the execution
 *
 * @author Sylvain PALOMINOS
 */
public interface DataProcessing {

    Class<? extends DataDescriptionType> getDataClass();

    /**
     * Preprocess the input/output to adapt it to the process (i.e. convert a File into a table name).
     * @param input The DescriptionType representing the input or output.
     * @param dataMap DataMap containing the input or output values.
     * @param pel ProcessExecutionListener to log the preprocessing (can be null).
     * @return Return a stash map containing information for post processing.
     */
    Map<URI, Object> preProcessData(DescriptionType input, Map<URI, Object> dataMap,
                                    ProcessExecutionListener pel);

    /**
     * Postprocess the input/output to adapt it to the process (i.e. convert a File into a table name).
     * @param input The DescriptionType representing the input or output.
     * @param dataMap DataMap containing the input or output values.
     * @param stash DataMap containing the information coming from the preprocessing.
     * @param pel ProcessExecutionListener to log the postprocessing (can be null).
     */
    void postProcessData(DescriptionType input, Map<URI, Object> dataMap, Map<URI, Object> stash,
                         ProcessExecutionListener pel);
}
